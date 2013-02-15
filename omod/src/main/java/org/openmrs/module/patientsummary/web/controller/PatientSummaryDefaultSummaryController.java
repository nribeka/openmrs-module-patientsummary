package org.openmrs.module.patientsummary.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientsummary.web.PatientSummaryWebConstants;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * This controller displays a patient summary
 * 
 */
@Controller
public class PatientSummaryDefaultSummaryController {

	// TODO should be a global property
	public static long WITHIN_TWELVE_MONTHS = 1000L * 60 * 60 * 24 * 365;	/* 1 Year */
	public static long WITHIN_SIX_MONTHS = 1000L * 60 * 60 * 24 * 182;	/* 6 months */
	
    /** Logger for this class and subclasses */
    protected final Log log = LogFactory.getLog(getClass());

	@RequestMapping(value = PatientSummaryWebConstants.MODULE_URL + "/defaultSummary")
	public void defaultSummary(ModelMap model, @RequestParam(required=true, value="patientId") Integer patientId) {

		if (!Context.isAuthenticated()) {
			return;
		}

		PatientService ps = Context.getPatientService();
		Patient p = null;

		try {
			p = ps.getPatient(patientId);
		}
		catch (Exception e) {
			log.warn("Unable to retrieve patient with patientId: " + patientId, e);
		}

		model.addAttribute("patient", p);

        // get non voided encounters
        List<Encounter> encounters = Context.getEncounterService().getEncountersByPatient(p);

        // get non voided drug orders
        List<DrugOrder> drugorders = Context.getOrderService()
                .getDrugOrdersByPatient(p, OrderService.ORDER_STATUS.NOTVOIDED,
                        false);

        List<Concept> conceptsToWatch;
        List<Concept> conceptsToGraph;
        List<Concept> vitalSignsConcepts;
        String conceptsIdsToWatch;
        String conceptsIdsToGraph;
        String vitalSignsConceptIds;
        int maxChildAge;

        // get child age
        maxChildAge = Integer.parseInt((Context.getAdministrationService()
                .getGlobalProperty("patientsummary.maxChildAge")));
        model.addAttribute("maxage", maxChildAge - 1); // p.getAge() doesn't count months,
        // so max needs correction        
        
        
        if (p.getAge() > maxChildAge) {

            // alert concepts
            conceptsIdsToWatch = Context.getAdministrationService()
                    .getGlobalProperty("patientsummary.adultconceptIdsToWatch");
            conceptsToWatch = delimitedStringToConceptList(conceptsIdsToWatch, ",");

            // concepts to graph
            conceptsIdsToGraph = Context.getAdministrationService()
                    .getGlobalProperty("patientsummary.adultconceptIdsToGraph");
            conceptsToGraph = delimitedStringToConceptList(conceptsIdsToGraph, ",");

            
            
            
            
        } else {

            // alert concepts
            conceptsIdsToWatch = Context.getAdministrationService()
                    .getGlobalProperty("patientsummary.childconceptIdsToWatch");
            conceptsToWatch = delimitedStringToConceptList(conceptsIdsToWatch,
                    ",");

            // concepts to graph
            conceptsIdsToGraph = Context.getAdministrationService()
                    .getGlobalProperty("patientsummary.childconceptIdsToGraph");
            conceptsToGraph = delimitedStringToConceptList(conceptsIdsToGraph, ",");

            vitalSignsConceptIds = Context.getAdministrationService()
                    .getGlobalProperty("patientsummary.vitalSigns");
            vitalSignsConcepts = delimitedStringToConceptList(
                    vitalSignsConceptIds, ",");
            model.addAttribute("vitals", prepareVitalsData(vitalSignsConcepts, p));
        }        
        
        // list of non voided adverse effects
        List<Obs> adverseEffect = 
        	getObservations(p, "patientsummary.adverseEffectConceptId");
        
        // list of non voided opportunistic infections
        List<Obs> opportunisticInfection = 
        	getObservations(p, "patientsummary.opportunisticInfectionConceptId");
                
        // list of non voided symptoms
        List<Obs> symptomPresent = 
        	getObservations(p, "patientsummary.symptomPresentConceptId");

        // previous non voided diagnosis
        List<Obs> prevDiag = 
        	getObservations(p, "patientsummary.previousDiagnosisConceptId");

        // lab test
        Integer labTestsConceptId = Integer.parseInt(Context
                .getAdministrationService().getGlobalProperty(
                        "patientsummary.labTestsConceptId"));
        List<Concept> labTestsConcepts = Context.getConceptService()
                .getConceptsByConceptSet(
                        (Context.getConceptService()
                                .getConcept(labTestsConceptId)));

        // note obs
        String conceptsIdsForNotes = Context.getAdministrationService()
                .getGlobalProperty("patientsummary.notesConceptIds");
        List<Concept> conceptsForNotes = delimitedStringToConceptList(
                conceptsIdsForNotes, ",");

        Map<Obs, String> notesText = new HashMap<Obs, String>();
        List<Obs> preparedNotes = prepareNotes(conceptsForNotes, p);

        for (Obs no : preparedNotes) {
            if (no.getValueText() != null) {
                String text = no.getValueText().replace("\n", "<p />");
                notesText.put(no, text);
            }
        }

        List<PatientProgram> patientPrograms = 
        	Context.getProgramWorkflowService().getPatientPrograms(
        			p, null, null, null, null, null, false);

        
        model.addAttribute("programs", patientPrograms);
        
        model.addAttribute("notes", preparedNotes);
        model.addAttribute("notesText", notesText);

        model.addAttribute("locale", Context.getLocale());
        model.addAttribute("agestring", getAgeString(p));
        model.addAttribute("alerts", prepareAlerts(conceptsToWatch, p));
        model.addAttribute("alertconcepts", conceptsIdsToWatch.split(","));
        model.addAttribute("lastencounter", getLastEncounter(encounters));
        model.addAttribute("infections", prepareMostRecentObs(opportunisticInfection));
        model.addAttribute("adverse", prepareMostRecentObs(adverseEffect));
        model.addAttribute("symptoms", prepareMostRecentObs(symptomPresent));
        model.addAttribute("prevdiags", prepareMostRecentObs(prevDiag));

        model.addAttribute("drugorders", drugorders);

        model.addAttribute("labdata", prepareLabData(labTestsConcepts, p,
                conceptsIdsToGraph));

        model.addAttribute("graphdata", prepareGraphData(conceptsToGraph, p));
        model.addAttribute("graphconcepts", conceptsIdsToGraph.split(","));

        List<Relationship> relationships = Context.getPersonService()
                .getRelationshipsByPerson(p);
        model.addAttribute("relationships", relationships);
    }

    // for given set of concepts to watch, find null concepts or concepts not
    // updated in the last 6 months
    private Map<String, Object> prepareAlerts(List<Concept> concepts, Patient p) {

        log.debug("Entering prepareAlerts");

        Map<String, Object> alertsMap = new HashMap<String, Object>();

        for (Concept c : concepts) {

            log.warn("looking for patients with obs for "
                    + c.getName().getName());
            List<Obs> obs = Context.getObsService()
                    .getObservationsByPersonAndConcept(p, c);

            if (obs == null || obs.isEmpty()) {
                alertsMap.put(c.getConceptId().toString(), " ");
                log
                        .warn("checking for obs revealed empty or null observations, so adding to NONE AT ALL group");
            } else {
                boolean isAdded = true;
                for (Obs o : obs) {
                    log.warn("this obs was in " + o.getObsDatetime());
                    if ((o.getObsDatetime().getTime() > (new Date()).getTime()
                            - WITHIN_SIX_MONTHS)
                            && o.getObsDatetime().getTime() < (new Date())
                                    .getTime()) {
                        isAdded = false;
                    }
                }

                if (isAdded) {
                    log
                            .warn("no cd4 counts seemed to have happened in past 6 months, so adding");
                    alertsMap.put(c.getConceptId().toString(),
                            "in the last 6 months");
                } else {
                    log.warn("a recent obs means we don't have to add");
                }
            }
        }

        return alertsMap;
    }

    private Map<String, Object> prepareMostRecentObs(List<Obs> obs) {     	
    	return prepareObs(obs, WITHIN_TWELVE_MONTHS);	
    }
    
    // for given set of obs, put recent (less than year old) in one set, and all
    // others in another set.
    private Map<String, Object> prepareObs(List<Obs> obs, long threshhold) {
    	
        Map<String, Object> obsMap = new HashMap<String, Object>();
        Set<Obs> recent = new HashSet<Obs>();
        Set<Obs> old = new HashSet<Obs>();
        Set<Obs> all = new HashSet<Obs>();
        
        if (obs != null && !obs.isEmpty()) {
            for (Obs o : obs) {
            	if ((o.getObsDatetime().getTime() > (new Date()).getTime() - threshhold)) {                	
                    recent.add(o);
                } else {
                    old.add(o);
                }
                all.add(o);
            }
        }
                
        obsMap.put("all", all);
        obsMap.put("recent", recent);
        obsMap.put("old", old);

        return obsMap;
    }
    
    
    /**
     * Returns a list of unique observations.  Also removes any concepts
     * that are marked to ignore.
     * @return
     */
    @SuppressWarnings("deprecation")
    public List<Obs> getUniqueObs(List<Obs> observations) {
    	
        List<Concept> ignoredConcepts = 
        	getConcepts("patientsummary.conceptsToIgnore", ",");

        if (ignoredConcepts == null) { 
        	ignoredConcepts = new ArrayList<Concept>();
        }
        
		HashMap<String, Obs> map = new LinkedHashMap<String, Obs>();
		for (Obs obs : observations) {
			log.info("Ignored concepts: " + ignoredConcepts);
			log.info("Obs concept: " + obs.getConcept());
			
			if (!ignoredConcepts.contains(obs.getValueCoded())) { 
				map.put(obs.getObsDatetime() + "." + 
						obs.getConcept().getConceptId() + "." + 
						obs.getValueAsString(), obs);
			}
		}
		return new ArrayList<Obs>(map.values());    
    }
    
    /**
     * Get most recent encounter for given set of encounters.
     * @param encounters
     * @return
     */
    private Map<String, Object> getLastEncounter(List<Encounter> encounters) {

        log.debug("Entering getLastEncounter");

        Map<String, Object> encounterMap = new HashMap<String, Object>();

        long timeago = 0;
        String timeagounit = null;
        encounterMap.put("datetime", new Date(0));

        for (Encounter e : encounters) {

            // find largest time greater than the previous and still in the
            // present
            if ((e.getEncounterDatetime().getTime() > ((Date) encounterMap
                    .get("datetime")).getTime())
                    && e.getEncounterDatetime().getTime() < (new Date())
                            .getTime()) {

                timeago = ((new Date()).getTime() - e.getEncounterDatetime()
                        .getTime())
                        / (1000L * 60 * 60 * 24);

                if (timeago > 0 && timeago < 31) {
                    timeagounit = timeago + " day";
                } else if (timeago > 30) {

                    timeago = timeago / 30;
                    timeagounit = timeago + " month";

                    if (timeago > 12) {
                        timeago = timeago / 12;
                        timeagounit = timeago + " year";
                    }
                }

                if (timeago == 1) {
                    timeagounit += " ago";
                } else {
                    timeagounit += "s ago";
                }

                encounterMap.put("timeago", timeagounit);
                encounterMap.put("datetime", e.getEncounterDatetime());
                encounterMap.put("provider", e.getProvider());
                encounterMap.put("location", e.getLocation());
                encounterMap.put("encounterType", (e.getEncounterType()!=null)?e.getEncounterType().getName():"");
            }

        }

        return encounterMap;

    }

    /**
     * Gets 
     * @param concepts
     * @param p
     * @return
     */
    private Map<String, Object> prepareVitalsData(List<Concept> concepts, Patient p) {

        log.debug("Entering prepareVitalsData");
        int mostRecentN = 3;

        Map<String, Object> vitalsMap = new HashMap<String, Object>();
        List<Obs> recent = new ArrayList<Obs>();
        List<Obs> old = new ArrayList<Obs>();

        List<Person> people = new ArrayList<Person>();
        people.add(p);

        
        List<Obs> obs = Context.getObsService().getObservations(people, null,
                concepts, null, null, null, null, mostRecentN, null, null, null, false);

        if (obs != null && !obs.isEmpty()) {

            for (Obs o : obs) {

                if ((o.getObsDatetime().getTime() > (new Date()).getTime()
                        - WITHIN_TWELVE_MONTHS)) {
                    recent.add(o);
                } else {
                    old.add(o);
                }
            }
        }

        vitalsMap.put("recent", recent);
        vitalsMap.put("old", old);

        return vitalsMap;
    }

    private List<Obs> prepareNotes(List<Concept> concepts, Patient p) {

        log.debug("Entering prepareNotes");

        int mostRecentN = 20;
        
        List<Obs> notes = new ArrayList<Obs>();

        List<Person> people = new ArrayList<Person>();
        people.add(p);

        List<Obs> obs = Context.getObsService().getObservations(people, null,
                concepts, null, null, null, null, mostRecentN, null, null, null, false);

        if (obs != null && !obs.isEmpty()) {
            for (Obs o : obs) {
            	if (o.getValueText() != null)
            		notes.add(o);
            }
        }

        return notes;
    }

    
    
    /**
     * 
     * @param concepts
     * @param p
     * @param conceptsIdsToGraph
     * @return
     */
    private Map<String, Object> prepareLabData(List<Concept> concepts, Patient p,
            String conceptsIdsToGraph) {

        log.debug("Entering prepareLabData");

        Map<String, Object> labDataMap = new HashMap<String, Object>();
        Set<Obs> recent = new HashSet<Obs>();
        Set<Obs> old = new HashSet<Obs>();
        Set<Obs> all = new HashSet<Obs>();
        
        for (Concept c : concepts) {

            List<Obs> obs = Context.getObsService()
                    .getObservationsByPersonAndConcept(p, c);

            if (obs != null && !obs.isEmpty()) {

                for (Obs o : obs) {
                	// Check if observation date is within than a year 
                    if ((o.getObsDatetime().getTime() 
                    		> (new Date()).getTime() - WITHIN_TWELVE_MONTHS)) {
                        //recent.add(o);
                    	recent.add(o);
                    } 
                    else {
                    	recent.add(o);
                        //old.add(o);
                    }
                    all.add(o);
                }
            }
        }
                
        
        labDataMap.put("all", all);
        labDataMap.put("recent", recent);
        labDataMap.put("old", old);

        return labDataMap;
    }

    
    /**
     * Returns a map of observations for concepts that can be charted.
     * @param concepts
     * @param p
     * @return
     */
    private Map<String, Object> prepareGraphData(List<Concept> concepts, Patient p) {

        log.debug("Entering prepareGraphData");

        Double floor = 0.0;
        Double ceiling = 0.0;

        Map<String, Object> graphDataMap = new HashMap<String, Object>();
        Map<String, Object> floorMap = new HashMap<String, Object>();
        Map<String, Object> ceilingMap = new HashMap<String, Object>();

        for (Concept c : concepts) {

            ConceptNumeric cn = null;
            List<Obs> graphobs = null;

            if (c.isNumeric()) {
                cn = (ConceptNumeric) c;
                graphobs = Context.getObsService()
                        .getObservationsByPersonAndConcept(p, c);

                for (Obs o : graphobs) {
                    Double cur = o.getValueNumeric();
                    if (cur > ceiling || ceiling == 0.0)
                        ceiling = cur;
                    if (cur < floor || floor == 0.0)
                        floor = cur;
                }

                if (floor == 0.0)
                    cn.getLowAbsolute();
                if (ceiling == 0.0)
                    cn.getHiAbsolute();

                ceiling += ceiling * .2;
                floor -= floor * .2;

                floorMap.put(c.getConceptId().toString(), floor);
                ceilingMap.put(c.getConceptId().toString(), ceiling);

                floor = 0.0;
                ceiling = 0.0;

            }

            graphDataMap.put("floor", floorMap);
            graphDataMap.put("ceiling", ceilingMap);
        }

        return graphDataMap;

    }

    /**
     * Parses a delimited string of concepts and returns a list of concepts.
     * @param delimitedString
     * @param delimiter
     * @return
     */
    private List<Concept> delimitedStringToConceptList(String delimitedString,
            String delimiter) {

        List<Concept> ret = null;

        if (delimitedString != null) {
            String[] tokens = delimitedString.split(delimiter);
            for (String token : tokens) {
                Integer conceptId = null;

                try {
                    conceptId = new Integer(token);
                } catch (NumberFormatException nfe) {
                    conceptId = null;
                }

                Concept c = null;

                if (conceptId != null) {
                    c = Context.getConceptService().getConcept(conceptId);
                } else {
                    c = Context.getConceptService().getConceptByName(token);
                }

                if (c != null) {
                    if (ret == null)
                        ret = new ArrayList<Concept>();
                    ret.add(c);
                }
            }
        }

        return ret;
    }
    
    
    /**
     * 
     */
    public List<Concept> getConcepts(String delimitedConcepts, String delimiter) { 
    	
    	String globalProperty = 
    		Context.getAdministrationService().getGlobalProperty(
    				"patientsummary.conceptsToIgnore");
    	
    	return delimitedStringToConceptList(globalProperty, delimiter);
   	
    }
    
    
    /**
     * Gets obversation by patient and concept.
     * 
     * @return	a list of observations
     */
    public List<Obs> getObservations(Patient patient, String globalProperty) { 
        
    	// TODO Allow multiple concepts in the global property
    	// TODO Add a way to filter out meaningless answers, like NONE (1107)
    	Integer conceptId = Integer.parseInt(Context
                .getAdministrationService().getGlobalProperty(globalProperty));

        Concept concept = Context.getConceptService().getConcept(conceptId);
        List<Obs> observations = 
        	Context.getObsService().getObservationsByPersonAndConcept(patient, concept);    	
    	
        
        return getUniqueObs(observations);
    }
    
    /**
     * Returns the patient's age as a string.
     * @param p
     * @return
     */
    private String getAgeString(Patient p) {
        Calendar cd = Calendar.getInstance();
        Calendar pbd = Calendar.getInstance();
        pbd.setTime(p.getBirthdate());
        int year = pbd.get(1);
        int month = pbd.get(2);
        int day = pbd.get(5);
        String yearUnit = "";
        String monthUnit = "";
        String dayUnit = "";
        Calendar bd = new GregorianCalendar(year, month, day);
        int ageYears = cd.get(1) - bd.get(1);
        int ageMonths;
        int ageDays;
        if (cd.before(new GregorianCalendar(cd.get(1), month, day))) {
            ageYears--;
            ageMonths = (12 - (bd.get(2) + 1)) + bd.get(2);
            if (day > cd.get(5))
                ageDays = day - cd.get(5);
            else if (day < cd.get(5))
                ageDays = cd.get(5) - day;
            else
                ageDays = 0;
        } else if (cd.after(new GregorianCalendar(cd.get(1), month, day))) {
            ageMonths = cd.get(2) - bd.get(2);
            if (day > cd.get(5))
                ageDays = day - cd.get(5) - day;
            else if (day < cd.get(5))
                ageDays = cd.get(5) - day;
            else
                ageDays = 0;
        } else {
            ageYears = cd.get(1) - bd.get(1);
            ageMonths = 0;
            ageDays = 0;
        }
        if (ageYears > 0)
            yearUnit = (new StringBuilder(String.valueOf(ageYears))).append(
                    " year").toString();
        if (ageYears > 1)
            yearUnit = (new StringBuilder(String.valueOf(yearUnit))).append(
                    "s,").toString();
        if (ageMonths > 0)
            monthUnit = (new StringBuilder(String.valueOf(ageMonths))).append(
                    " month").toString();
        if (ageMonths > 1)
            monthUnit = (new StringBuilder(String.valueOf(monthUnit))).append(
                    "s,").toString();
        if (ageDays > 0)
            dayUnit = (new StringBuilder(String.valueOf(ageDays))).append(
                    " day").toString();
        if (ageDays > 1)
            dayUnit = (new StringBuilder(String.valueOf(dayUnit))).append("s")
                    .toString();
        return (new StringBuilder(String.valueOf(yearUnit))).append(" ")
                .append(monthUnit).append(" ").append(dayUnit).toString();
    }
        	
}

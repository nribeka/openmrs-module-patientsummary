$j(document).ready(function() {
    $j('#tabs').tabs();

    $j('#textarea-container').resizable({
        handles: 's',
        alsoResize: 'iframe',
        minHeight: 220,
        maxHeight: 475
    });

    $j('#executeButton').click(function(event) {
        exec();
    });
    
    $j('#script-name').click(function(event) {
    	$j('#rename-dialog').dialog('open');
    });

    var newName = $j("#new-name"),
		allFields = $j([]).add(newName);
    var tips = $j(".validateTips");

    // Prevent enter key from submitting page in name dialog
    newName.keydown(function(event) {
        if (event.which === $j.ui.keyCode.ENTER) {
        	$j('.ui-dialog-buttonpane > button:first').click();
            event.preventDefault();
        }
    });
    
    function updateTips(t) {
		tips
			.text(t)
			.addClass('ui-state-highlight');
		setTimeout(function() {
			tips.removeClass('ui-state-highlight', 1500);
		}, 500);
    }

    function checkLength(o,n,min,max) {

		if ( o.val().length > max || o.val().length < min ) {
			o.addClass('ui-state-error');
			updateTips($j('#invalid-name-length-msg').text());
			return false;
		} else {
			return true;
		}

    }

	function checkRegexp(o,regexp,n) {

		if ( !( regexp.test( o.val() ) ) ) {
			o.addClass('ui-state-error');
			updateTips(n);
			return false;
		} else {
			return true;
		}

	}

	// Setup dialog buttons using localized text from hidden divs
	var nameDialogButtons = {};
	nameDialogButtons[$j('#rename-dialog-submit').text()] = function() {
		var bValid = true;
		allFields.removeClass('ui-state-error');

		bValid = bValid && checkLength(newName,'name',1,50);
		bValid = bValid && checkRegexp(newName,/^[a-z]([0-9a-z_ \-])*$/i,$j('#invalid-name-pattern-msg').text());
		
		if (bValid) {
			$j('#name').val(newName.val());
			$j('#script-name').text(newName.val());
			$j(this).dialog('close');
		}
	};
	nameDialogButtons[$j('#rename-dialog-cancel').text()] = function() {
		$j(this).dialog('close');
	};
	
	$j('#rename-dialog').dialog({
		autoOpen: false,
		position: ['center', 50],
		height: 250,
		width: 400,
		modal: true,
		resizable: false,
		buttons: nameDialogButtons,
		open: function() {
			// clear any old tips
			tips.text('');
			tips.removeClass('ui-state-highlight');
			allFields.removeClass('ui-state-error');
			
			// initialize new name value and grab focus
			newName.val($j('#name').val());
			newName.focus();
			newName.select();		    
		},
		close: function() {
			allFields.val('').removeClass('ui-state-error');
		}
	});

});

function exec() {
    $j('#executeButton').attr('disabled', true);
    $j('#tabs').tabs('select', 1);
    $j('#output').html($j('#running-html').html()).fadeIn();
    var script = editor.getCode();
    DWRGroovyService.eval(script, function(data) {
        $j('#output').html("").fadeIn();
        $j('#result').html("");
        $j('#stacktrace').html("");

        // display result
        if (data[0] == "Insufficient Privileges") {
            $j("#noPrivileges").fadeIn();
        } else if (data[0] != "null") {
            $j('#tabs').tabs('select', 0);
            $j('#result').html(data[0]).fadeIn();
        } else {
            $j('#result').fadeOut();
        }

        // display output
        if (data[1] != "") {
            $j('#tabs').tabs('select', 1);
            $j('#output').html(data[1]).fadeIn();
        } else {
            $j('#output').fadeOut();
        }

        // display stacktrace
        if (data[2] != "") {
            $j('#tabs').tabs('select', 2);
            $j('#stacktrace').html(data[2]).fadeIn();
        } else {
            $j('#stacktrace').fadeOut();
        }
        $j('#executeButton').attr('disabled', false);
    });
}
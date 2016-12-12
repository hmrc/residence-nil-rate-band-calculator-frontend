$(document).ready(function() {

// =====================================================
// Initialise show-hide-content
// Toggles additional content based on radio/checkbox input state
// =====================================================
    var showHideContent = new GOVUK.ShowHideContent()
    showHideContent.init()


// =====================================================
// Adds data-focuses attribute to all containers of inputs listed in an error summary
// This allows validatorFocus to bring viewport to correct scroll point
// =====================================================
    function assignFocus () {
        var counter = 0;
        $('.error-summary-list a').each(function(){
            var linkhash = $(this).attr("href").split('#')[1];
            $('#' + linkhash).parents('.form-field').first().attr('id', 'f-' + counter);
            $(this).attr('data-focuses', 'f-' + counter);
            counter++;
        });
    }
    assignFocus();

});

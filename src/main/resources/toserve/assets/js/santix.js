
function errorShake(){

    $( "#signupError").effect( "shake" );
    $( "#loginError").effect( "shake" );
    log.console('shake');
}


jQuery('.numbersOnly').keyup(function () { 
    this.value = this.value.replace(/[^0-9\.]/g,'');
});

jQuery("#reset").hide();

var Elastisearch, errorShake;

Elastisearch = (function() {
  function Elastisearch() {}

  Elastisearch.searchTerm = function() {
    return {
      query: {
        term: {
          'name.autocomplete': $('#term').val()
        }
      },
      facets: {
        name: {
          terms: {
            field: 'name'
          }
        }
      }
    };
  };

  return Elastisearch;

})();

Elastisearch.searchTerm();

errorShake = function() {
  $('#signupError').effect('shake');
  $('#loginError').effect('shake');
  log.console('shake');
};

jQuery('.numbersOnly').keyup(function() {
  this.value = this.value.replace(/[^0-9\.]/g, '');
});

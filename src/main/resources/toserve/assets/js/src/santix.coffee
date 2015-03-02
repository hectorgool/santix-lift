
class Elastisearch
	@searchTerm: ->
		{
			query: term: 'name.autocomplete': $('#term').val()
			facets: name: terms: field: 'name'
		}

Elastisearch.searchTerm()

errorShake = ->
  $('#signupError').effect 'shake'
  $('#loginError').effect 'shake'
  log.console 'shake'
  return

jQuery('.numbersOnly').keyup ->
  @value = @value.replace(/[^0-9\.]/g, '')
  return
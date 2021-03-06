package ozone.owf.grails.test.integration

import grails.converters.JSON;
import grails.test.mixin.TestMixin
import grails.test.mixin.integration.IntegrationTestMixin

import ozone.owf.grails.controllers.PersonWidgetDefinitionController
import ozone.owf.grails.domain.ERoleAuthority
import ozone.owf.grails.domain.Intent
import ozone.owf.grails.domain.IntentDataType
import ozone.owf.grails.domain.Group
import ozone.owf.grails.domain.Person
import ozone.owf.grails.domain.RelationshipType
import ozone.owf.grails.domain.WidgetDefinition
import ozone.owf.grails.domain.WidgetDefinitionIntent
import ozone.owf.grails.domain.PersonWidgetDefinition
import ozone.owf.grails.OwfException
import ozone.owf.grails.services.DomainMappingService


@TestMixin(IntegrationTestMixin)
class PersonWidgetDefinitionControllerTest extends OWFGroovyTestCase {

	def widgetDefinitionService
	def personWidgetDefinitionService
    def serviceModelService
	def controller

	void testListUserAndGroupWidgets() {
		loginAsUsernameAndRole('testAdmin1', 'role')
		createWidgetDefinitionForTest()

		controller = new PersonWidgetDefinitionController()
		controller.widgetDefinitionService = widgetDefinitionService
		controller.request.contentType = "text/json"
		controller.listUserAndGroupWidgets()

		assert "0c5435cf-4021-4f2a-ba69-dde451d12551" == JSON.parse(controller.response.contentAsString).id[0]
	}

	void testListUserAndGroupWidgetsByName() {
		loginAsUsernameAndRole('testAdmin1', 'role')
		createWidgetDefinitionForTest()

		controller = new PersonWidgetDefinitionController()
		controller.widgetDefinitionService = widgetDefinitionService
		controller.request.contentType = "text/json"
		controller.request.parameters = [widgetName:'%C%']
		controller.listUserAndGroupWidgets()

		assert "0c5435cf-4021-4f2a-ba69-dde451d12551" == JSON.parse(controller.response.contentAsString).id[0]
	}

	void testListUserAndGroupWidgetsByExactName() {
		loginAsUsernameAndRole('testAdmin1', 'role')
		createWidgetDefinitionForTest()

		controller = new PersonWidgetDefinitionController()
		controller.widgetDefinitionService = widgetDefinitionService
		controller.request.contentType = "text/json"
		controller.request.parameters = [widgetName:'Widget C']
		controller.listUserAndGroupWidgets()

		assert "0c5435cf-4021-4f2a-ba69-dde451d12551" == JSON.parse(controller.response.contentAsString).id[0]
	}

	void testListUserAndGroupWidgetsByVersion() {
		loginAsUsernameAndRole('testAdmin1', 'role')
		createWidgetDefinitionForTest()

		controller = new PersonWidgetDefinitionController()
		controller.widgetDefinitionService = widgetDefinitionService
		controller.request.contentType = "text/json"
		controller.request.parameters = [widgetVersion:'1.0']
		controller.listUserAndGroupWidgets()

		assert "0c5435cf-4021-4f2a-ba69-dde451d12551" == JSON.parse(controller.response.contentAsString).id[0]
	}

	void testListUserAndGroupWidgetsByWidgetGuid() {
		loginAsUsernameAndRole('testAdmin1', 'role')
		createWidgetDefinitionForTest()

		controller = new PersonWidgetDefinitionController()
		controller.widgetDefinitionService = widgetDefinitionService
		controller.request.contentType = "text/json"
		controller.request.parameters = [widgetGuid:'0c5435cf-4021-4f2a-ba69-dde451d12551']
		controller.listUserAndGroupWidgets()

		assert "0c5435cf-4021-4f2a-ba69-dde451d12551" == JSON.parse(controller.response.contentAsString).id[0]
	}

	void testListUserAndGroupWidgetsByUniqueId() {
		loginAsUsernameAndRole('testAdmin1', 'role')
		createWidgetDefinitionForTest()

		controller = new PersonWidgetDefinitionController()
		controller.widgetDefinitionService = widgetDefinitionService
		controller.request.contentType = "text/json"
		controller.request.parameters = [universalName:'com.company.widget.uuid']
		controller.listUserAndGroupWidgets()

		assert "0c5435cf-4021-4f2a-ba69-dde451d12551" == JSON.parse(controller.response.contentAsString).id[0]
	}

	void testBulkDelete() {
		loginAsUsernameAndRole('testAdmin1', 'role')
		createWidgetDefinitionForTest()

		controller = new PersonWidgetDefinitionController()
		controller.personWidgetDefinitionService = personWidgetDefinitionService
		controller.request.contentType = "text/json"
		controller.params.widgetGuidsToDelete = '["0c5435cf-4021-4f2a-ba69-dde451d12551"]'

		controller.bulkDelete()

		assert JSON.parse(controller.response.contentAsString).success
	}

	void testBulkUpdate() {
		loginAsUsernameAndRole('testAdmin1', 'role')
		createWidgetDefinitionForTest()

		controller = new PersonWidgetDefinitionController()
		controller.personWidgetDefinitionService = personWidgetDefinitionService
		controller.request.contentType = "text/json"
		controller.params.widgetsToUpdate = '[{"guid":"0c5435cf-4021-4f2a-ba69-dde451d12551", "visible":true}]'

		controller.bulkUpdate()

		assert JSON.parse(controller.response.contentAsString).success
	}

	void testBulkDeleteWithoutParams() {
		loginAsUsernameAndRole('testAdmin1', 'role')
		createWidgetDefinitionForTest()

			controller = new PersonWidgetDefinitionController()
			controller.personWidgetDefinitionService = personWidgetDefinitionService
			controller.request.contentType = "text/json"

				controller.bulkDelete()
				assert '"Error during bulkDelete: The requested entity failed to pass validation. A fatal validation error occurred. WidgetsToDelete param required. Params: [:]"' == controller.response.contentAsString
	}

	void testBulkDeleteAndUpdateWithoutParams() {
		loginAsUsernameAndRole('testAdmin1', 'role')
		createWidgetDefinitionForTest()

			controller = new PersonWidgetDefinitionController()
			controller.personWidgetDefinitionService = personWidgetDefinitionService
			controller.request.contentType = "text/json"

				controller.bulkDeleteAndUpdate()
				assert '"Error during bulkDeleteAndUpdate: The requested entity failed to pass validation. A fatal validation error occurred. WidgetsToDelete param required. Params: [:]"' == controller.response.contentAsString
	}

	void testBulkUpdateWithoutParams() {
		loginAsUsernameAndRole('testAdmin1', 'role')
		createWidgetDefinitionForTest()

			controller = new PersonWidgetDefinitionController()
			controller.personWidgetDefinitionService = personWidgetDefinitionService
			controller.request.contentType = "text/json"

				controller.bulkUpdate()
				assert '"Error during bulkUpdate: The requested entity failed to pass validation. A fatal validation error occurred. WidgetsToUpdate param required. Params: [:]"' == controller.response.contentAsString
	}

	void testListByWidgetNameButNotFound() {
        loginAsUsernameAndRole('testAdmin1', 'role')
        createWidgetDefinitionForTest()

		controller = new PersonWidgetDefinitionController()
		controller.personWidgetDefinitionService = personWidgetDefinitionService
		controller.request.contentType = "text/json"
		controller.request.parameters = [widgetName:'1']
		controller.list()

		assert null == JSON.parse(controller.response.contentAsString)[0]
	}

	void testListByWidgetName() {
		loginAsUsernameAndRole('testAdmin1', 'role')
		createWidgetDefinitionForTest()

		controller = new PersonWidgetDefinitionController()
		controller.personWidgetDefinitionService = personWidgetDefinitionService
		controller.request.contentType = "text/json"
		controller.request.parameters = [widgetName:'%C%']
		controller.list()

		assert "0c5435cf-4021-4f2a-ba69-dde451d12551" == JSON.parse(controller.response.contentAsString)[0].path
	}

	void testNotAuthorizedToCreateAWidget() {
	   loginAsUsernameAndRole('testAdmin1', 'role')
	   def person = Person.build();

	   def widgetDefinition = WidgetDefinition.build(displayName : 'Widget C',
 		                                    height : 740,
 		                                    imageUrlMedium : '../images/blue/icons/widgetIcons/widgetC.gif',
 		                                    imageUrlSmall : '../images/blue/icons/widgetContainer/widgetCsm.gif',
 		                                    widgetGuid : '0c5435cf-4021-4f2a-ba69-dde451d12551',
                                            universalName : '0c5435cf-4021-4f2a-ba69-dde451d12551',
                                            widgetVersion : '1.0',
 		                                    widgetUrl : '../examples/fake-widgets/widget-c.html',
 		                                    width : 980)
	   controller = new PersonWidgetDefinitionController()
   	 controller.personWidgetDefinitionService = personWidgetDefinitionService
   	 controller.request.contentType = "text/json"
 		 controller.request.parameters = [guid:'0c5435cf-4021-4f2a-ba69-dde451d12551', personId:"#{person.id}", windowname:"true"]
 		 controller.create()

 		 System.out.println("ResponseString: " + controller.modelAndView)
 		 //assert "\"Error during create: You are not authorized to access this entity. You are not authorized to create widgets for other users.\"".equals(controller.response.contentAsString)
	}

	void testDuplicateWidget() {
	   createWidgetDefinitionForTest()
	   loginAsUsernameAndRole('testAdmin1', 'role')
	   controller = new PersonWidgetDefinitionController()
   	 controller.personWidgetDefinitionService = personWidgetDefinitionService
   	 controller.request.contentType = "text/json"
 		 controller.request.parameters = [guid:'0c5435cf-4021-4f2a-ba69-dde451d12551', windowname:"true"]
 		 controller.create()

 		 System.out.println("ResponseString: " + controller.modelAndView)
 		 //assert "\"Error during create: You are not authorized to access this entity. You are not authorized to create widgets for other users.\"".equals(controller.response.contentAsString)
	}

	void testCreatePersonWidgetDefinition() {
		loginAsUsernameAndRole('testAdmin1', ERoleAuthority.ROLE_ADMIN.strVal)

        def person = Person.findByUsername('testAdmin1')
	    def widgetDefinition = WidgetDefinition.build(displayName : 'Widget C',
	    											  height : 740,
		                                              imageUrlMedium : '../images/blue/icons/widgetIcons/widgetC.gif',
		                                              imageUrlSmall : '../images/blue/icons/widgetContainer/widgetCsm.gif',
		                                              widgetGuid : '0c5435cf-4021-4f2a-ba69-dde451d12551',
                                                      universalName : '0c5435cf-4021-4f2a-ba69-dde451d12551',
                                                      widgetVersion : '1.0',
		                                              widgetUrl : '../examples/fake-widgets/widget-c.html',
		                                              width : 980)

		controller = new PersonWidgetDefinitionController()
		controller.personWidgetDefinitionService = personWidgetDefinitionService
		controller.request.contentType = "text/json"

		controller.params.guid = '0c5435cf-4021-4f2a-ba69-dde451d12551'
		controller.params.personId = person.id
		controller.create()

		assert 'Widget C' == JSON.parse(controller.response.contentAsString).value.namespace
		assert '0c5435cf-4021-4f2a-ba69-dde451d12551' == JSON.parse(controller.response.contentAsString).path
	}

	void testCreatePersonWidgetDefinitionByUnknownUser() {
		loginAsUsernameAndRole('testAdmin1', ERoleAuthority.ROLE_ADMIN.strVal)

		def person = Person.findByUsername('testAdmin1')
		def widgetDefinition = WidgetDefinition.build(displayName : 'Widget C',
				height : 740,
				imageUrlMedium : '../images/blue/icons/widgetIcons/widgetC.gif',
				imageUrlSmall : '../images/blue/icons/widgetContainer/widgetCsm.gif',
				widgetGuid : '0c5435cf-4021-4f2a-ba69-dde451d12551',
                universalName : '0c5435cf-4021-4f2a-ba69-dde451d12551',
                widgetVersion : '1.0',
				widgetUrl : '../examples/fake-widgets/widget-c.html',
				width : 980)

		controller = new PersonWidgetDefinitionController()
		controller.personWidgetDefinitionService = personWidgetDefinitionService
		controller.request.contentType = "text/json"

		controller.params.guid = '0c5435cf-4021-4f2a-ba69-dde451d12551'
		controller.params.personId = 100000  // Unknown user
		controller.create()

		assert '"Error during create: The requested entity was not found. Person with id of 100000 not found while attempting to create a widget for a user."' == controller.response.contentAsString
		assert null ==  PersonWidgetDefinition.findByWidgetDefinitionAndPerson(widgetDefinition, person)
	}

	void testCreatePersonWidgetDefinitionByUnauthorizedUser() {
		loginAsUsernameAndRole('testUser1', ERoleAuthority.ROLE_USER.strVal)

		def person = Person.findByUsername('testUser2')
		def widgetDefinition = WidgetDefinition.build(displayName : 'Widget C',
				height : 740,
				imageUrlMedium : '../images/blue/icons/widgetIcons/widgetC.gif',
				imageUrlSmall : '../images/blue/icons/widgetContainer/widgetCsm.gif',
				widgetGuid : '0c5435cf-4021-4f2a-ba69-dde451d12551',
                universalName : '0c5435cf-4021-4f2a-ba69-dde451d12551',
                widgetVersion : '1.0',
				widgetUrl : '../examples/fake-widgets/widget-c.html',
				width : 980)

		controller = new PersonWidgetDefinitionController()
		controller.personWidgetDefinitionService = personWidgetDefinitionService
		controller.request.contentType = "text/json"

		controller.params.guid = '0c5435cf-4021-4f2a-ba69-dde451d12551'
		controller.params.personId = person.id // Unauthorized user
		controller.create()

		assert '"Error during create: You are not authorized to access this entity. You are not authorized to create widgets for other users."' == controller.response.contentAsString
		assert null ==  PersonWidgetDefinition.findByWidgetDefinitionAndPerson(widgetDefinition, person)
	}

	void testShowPersonWidgetDefinitionByGuid() {
		loginAsUsernameAndRole('testAdmin1', ERoleAuthority.ROLE_ADMIN.strVal)
		createWidgetDefinitionForTest()

		controller = new PersonWidgetDefinitionController()
		controller.personWidgetDefinitionService = personWidgetDefinitionService
		controller.request.contentType = "text/json"

		controller.params.guid = '0c5435cf-4021-4f2a-ba69-dde451d12551'
		controller.show()

		assert 'Widget C' == JSON.parse(controller.response.contentAsString).value.namespace
		assert '0c5435cf-4021-4f2a-ba69-dde451d12551' == JSON.parse(controller.response.contentAsString).path
	}

	void testShowNonexistentPersonWidgetDefinition() {
		loginAsUsernameAndRole('testAdmin1', ERoleAuthority.ROLE_ADMIN.strVal)

		def person = Person.findByUsername('testUser1')
		def widgetDefinition = WidgetDefinition.build(displayName : 'Widget C',
													  height : 740,
													  imageUrlMedium : '../images/blue/icons/widgetIcons/widgetC.gif',
													  imageUrlSmall : '../images/blue/icons/widgetContainer/widgetCsm.gif',
													  widgetGuid : '0c5435cf-4021-4f2a-ba69-dde451d12551',
                                                      universalName : '0c5435cf-4021-4f2a-ba69-dde451d12551',
                                                      widgetVersion : '1.0',
													  widgetUrl : '../examples/fake-widgets/widget-c.html',
													  width : 980)

		controller = new PersonWidgetDefinitionController()
		controller.personWidgetDefinitionService = personWidgetDefinitionService
		controller.request.contentType = "text/json"

		controller.params.guid = '0c5435cf-4021-4f2a-ba69-dde451d12551'
		controller.show()

		assert '"Error during show: The requested entity was not found. Widget with guid of 0c5435cf-4021-4f2a-ba69-dde451d12551 not found."' == controller.response.contentAsString
		assert null ==  PersonWidgetDefinition.findByWidgetDefinitionAndPerson(widgetDefinition, person)
	}

	void testShowPersonWidgetDefinitionWithUuid() {
		loginAsUsernameAndRole('testAdmin1', ERoleAuthority.ROLE_ADMIN.strVal)
		createWidgetDefinitionForTest()

		controller = new PersonWidgetDefinitionController()
		controller.personWidgetDefinitionService = personWidgetDefinitionService
		controller.request.contentType = "text/json"

		controller.params.guid = '0c5435cf-4021-4f2a-ba69-dde451d12551'
		controller.params.universalName = 'com.company.widget.uuid'
		controller.show()

		assert 'Widget C' == JSON.parse(controller.response.contentAsString).value.namespace
		assert '0c5435cf-4021-4f2a-ba69-dde451d12551' == JSON.parse(controller.response.contentAsString).path
	}

	void testShowPersonWidgetDefinitionByUniqueIdWithoutUuid() {
		loginAsUsernameAndRole('testAdmin1', ERoleAuthority.ROLE_ADMIN.strVal)
		createWidgetDefinitionForTest()

		controller = new PersonWidgetDefinitionController()
		controller.personWidgetDefinitionService = personWidgetDefinitionService
		controller.request.contentType = "text/json"

		controller.params.guid = 'com.company.widget.uuid'
		controller.show()

		assert '"Error during show: The requested entity was not found. Widget with guid of com.company.widget.uuid not found."' == controller.response.contentAsString
	}

	void testShowPersonWidgetDefinitionByUuid() {
		loginAsUsernameAndRole('testAdmin1', ERoleAuthority.ROLE_ADMIN.strVal)
		createWidgetDefinitionForTest()

		controller = new PersonWidgetDefinitionController()
		controller.personWidgetDefinitionService = personWidgetDefinitionService
		controller.request.contentType = "text/json"

		controller.params.universalName = 'com.company.widget.uuid'
		controller.show()

		assert 'Widget C' == JSON.parse(controller.response.contentAsString).value.namespace
		assert '0c5435cf-4021-4f2a-ba69-dde451d12551' == JSON.parse(controller.response.contentAsString).path
	}

	void testListPersonWidgetDefinition() {
		loginAsUsernameAndRole('testAdmin1', ERoleAuthority.ROLE_ADMIN.strVal)
		createWidgetDefinitionForTest('Widget C','widgetC.gif','widgetCsm.gif','0c5435cf-4021-4f2a-ba69-dde451d12551','widget-c.html', 1)
		createWidgetDefinitionForTest('Widget D','widgetD.gif','widgetDsm.gif','0c5435cf-4021-4f2a-ba69-dde451d12552','widget-d.html', 2)

		controller = new PersonWidgetDefinitionController()
		controller.personWidgetDefinitionService = personWidgetDefinitionService
		controller.request.contentType = "text/json"

		controller.params.widgetName = '%Widget%'
		controller.list()

		assert 2 == JSON.parse(controller.response.contentAsString).size()
		assert 'Widget C' == JSON.parse(controller.response.contentAsString)[0].value.namespace
		assert 'Widget D' == JSON.parse(controller.response.contentAsString)[1].value.namespace
	}

	void testUpdatePersonWidgetDefinition() {
		loginAsUsernameAndRole('testAdmin1', ERoleAuthority.ROLE_ADMIN.strVal)
		createWidgetDefinitionForTest()

		def person = Person.findByUsername('testAdmin1')
		def personWidgetDefinition = PersonWidgetDefinition.findByPerson(person)

		assert 'Widget C' == personWidgetDefinition.widgetDefinition.displayName
		personWidgetDefinition.widgetDefinition.displayName = 'Widget D'

		controller = new PersonWidgetDefinitionController()
		controller.personWidgetDefinitionService = personWidgetDefinitionService
		controller.request.contentType = "text/json"

		controller.params.guid = personWidgetDefinition.widgetDefinition.widgetGuid
		controller.params.personId = person.id
		controller.update()

		assert 'Widget D' == JSON.parse(controller.response.contentAsString).value.namespace
		assert 'Widget C' != JSON.parse(controller.response.contentAsString).value.namespace
	}

	void testUpdateNonexistentPersonWidgetDefinition() {
		loginAsUsernameAndRole('testAdmin1', ERoleAuthority.ROLE_ADMIN.strVal)
		createWidgetDefinitionForTest()

		def person = Person.findByUsername('testAdmin1')
		def widgetDefinition = WidgetDefinition.findByWidgetGuid('0c5435cf-4021-4f2a-ba69-dde451d12551')

		controller = new PersonWidgetDefinitionController()
		controller.personWidgetDefinitionService = personWidgetDefinitionService
		controller.request.contentType = "text/json"

		controller.params.guid = '0c5435cf-4021-4f2a-ba69-dde451d12559'
		controller.params.personId = person.id
		controller.update()

		assert '"Error during update: The requested entity was not found. Widget 0c5435cf-4021-4f2a-ba69-dde451d12559 not found."' == controller.response.contentAsString
		assert null != PersonWidgetDefinition.findByWidgetDefinitionAndPerson(widgetDefinition, person)
	}

	void testDeletePersonWidgetDefinitionByWidgetGuidAndUsername() {
		loginAsUsernameAndRole('testAdmin1', ERoleAuthority.ROLE_ADMIN.strVal)
		createWidgetDefinitionForTest()

		def person = Person.findByUsername('testAdmin1')
		def personWidgetDefinition = PersonWidgetDefinition.findByPerson(person)
		def widgetDefinition = personWidgetDefinition.widgetDefinition

		controller = new PersonWidgetDefinitionController()
		controller.personWidgetDefinitionService = personWidgetDefinitionService
		controller.request.contentType = "text/json"

		controller.params.guid = personWidgetDefinition.widgetDefinition.widgetGuid
		controller.params.username = person.username
		controller.params.adminEnabled = true
		controller.delete()

		assert 'Widget C' == JSON.parse(controller.response.contentAsString).value.namespace
		assert '0c5435cf-4021-4f2a-ba69-dde451d12551' == JSON.parse(controller.response.contentAsString).path
		assert null ==  PersonWidgetDefinition.findByWidgetDefinitionAndPerson(widgetDefinition, person)
	}

	void testDeletePersonWidgetDefinitionByWidgetGuidAndPersonId() {
		loginAsUsernameAndRole('testAdmin1', ERoleAuthority.ROLE_ADMIN.strVal)
		createWidgetDefinitionForTest()

		def person = Person.findByUsername('testAdmin1')
		def personWidgetDefinition = PersonWidgetDefinition.findByPerson(person)
		def widgetDefinition = personWidgetDefinition.widgetDefinition

		controller = new PersonWidgetDefinitionController()
		controller.personWidgetDefinitionService = personWidgetDefinitionService
		controller.request.contentType = "text/json"

		controller.params.guid = personWidgetDefinition.widgetDefinition.widgetGuid
		controller.params.personId = person.id
		controller.params.adminEnabled = true
		controller.delete()

		assert 'Widget C' == JSON.parse(controller.response.contentAsString).value.namespace
		assert '0c5435cf-4021-4f2a-ba69-dde451d12551' == JSON.parse(controller.response.contentAsString).path
		assert null ==  PersonWidgetDefinition.findByWidgetDefinitionAndPerson(widgetDefinition, person)
	}

	void testDeletePersonWidgetDefinitionByWidgetGuid() {
		loginAsUsernameAndRole('testAdmin1', ERoleAuthority.ROLE_ADMIN.strVal)
		createWidgetDefinitionForTest()

		def widgetDefinition = WidgetDefinition.findByDisplayName('Widget C')

		controller = new PersonWidgetDefinitionController()
		controller.personWidgetDefinitionService = personWidgetDefinitionService
		controller.request.contentType = "text/json"

		controller.params.guid = widgetDefinition.widgetGuid
		controller.delete()

		assert 'Widget C' == JSON.parse(controller.response.contentAsString).value.namespace
		assert '0c5435cf-4021-4f2a-ba69-dde451d12551' == JSON.parse(controller.response.contentAsString).path
		assert null ==  PersonWidgetDefinition.findByWidgetDefinition(widgetDefinition)
	}

	void createWidgetDefinitionForTest() {
		def person = Person.findByUsername('testAdmin1')
		def widgetDefinition = WidgetDefinition.build(displayName : 'Widget C',
		                                    height : 740,
		                                    imageUrlMedium : '../images/blue/icons/widgetIcons/widgetC.gif',
		                                    imageUrlSmall : '../images/blue/icons/widgetContainer/widgetCsm.gif',
		                                    widgetGuid : '0c5435cf-4021-4f2a-ba69-dde451d12551',
                                            universalName : 'com.company.widget.uuid',
                                            widgetVersion : '1.0',
                                            widgetUrl : '../examples/fake-widgets/widget-c.html',
		                                    width : 980)
    	def personWidgetDefinition = PersonWidgetDefinition.build(person: person,
                                 widgetDefinition: widgetDefinition,
                                 visible : true,
                                 pwdPosition: 1)
	}

	def createWidgetDefinitionForTest(widgetName, imageUrlMedium, imageUrlSml, guid, widgetUrl, pwdPosition) {

		def person = Person.findByUsername('testAdmin1')
		def widgetDefinition = WidgetDefinition.build(displayName : widgetName,
				height : 740,
				imageUrlMedium : '../images/blue/icons/widgetIcons/' + imageUrlMedium,
				imageUrlSmall : '../images/blue/icons/widgetContainer/' + imageUrlSml,
				widgetGuid : guid,
                universalName : guid,
                widgetVersion : '1.0',
				widgetUrl : '../examples/fake-widgets/' + widgetUrl,
				width : 980)
		def personWidgetDefinition = PersonWidgetDefinition.build(person: person,
				widgetDefinition: widgetDefinition,
				visible : true,
				pwdPosition: pwdPosition)

        widgetDefinition.save(flush:true)
        return personWidgetDefinition
	}

    void createWidgetDefinitionsForTest(numWidgetDefinitions) {
      def person = Person.findByUsername('testAdmin1')

      for (int i = 0 ; i < numWidgetDefinitions ; i++) {
        def widgetDefinition = WidgetDefinition.build(displayName : 'Widget '+i,
                                              height : 740,
                                              imageUrlMedium : '../images/blue/icons/widgetIcons/widgetC.gif',
                                              imageUrlSmall : '../images/blue/icons/widgetContainer/widgetCsm.gif',
                                              widgetGuid : java.util.UUID.randomUUID().toString(),
                                              universalName : java.util.UUID.randomUUID().toString(),
                                              widgetVersion : '1.0',
                                              widgetUrl : '../examples/fake-widgets/widget-c.html',
                                              width : 980)
        def personWidgetDefinition = PersonWidgetDefinition.build(person: person,
                                     widgetDefinition: widgetDefinition,
                                     visible : true,
                                     pwdPosition: i)

      }
    }

    def createWidgetDefinitionIntentForTest(widgetDefinition, intent, dataTypes, send, receive) {
        def widgetDefinitionIntent = WidgetDefinitionIntent.build(
            widgetDefinition : widgetDefinition,
            intent : intent,
            dataTypes : dataTypes,
            send : send,
            receive : receive)

        widgetDefinitionIntent.save(flush:true)
    }
}

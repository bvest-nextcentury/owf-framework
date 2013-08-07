import grails.util.GrailsUtil
import ozone.owf.grails.OwfExceptionResolver
import ozone.owf.grails.services.AutoLoginAccountService
import ozone.owf.grails.domain.ERoleAuthority
import ozone.owf.grails.services.AccountService
import ozone.owf.nineci.hibernate.AuditTrailInterceptor

beans = {

	xmlns context: 'http://www.springframework.org/schema/context'
	context.'component-scan'('base-package': 'org.ozoneplatform.appconfig.server')
	context.'component-scan'('base-package': 'ozone.owf.util')
	
    auditLogListener(org.ozoneplatform.auditing.AuditLogListener) {
        sessionFactory = ref('sessionFactory')
        accountService = ref('accountService')
        owfApplicationConfigurationService = ref('owfApplicationConfigurationService')
        grailsApplication = ref('grailsApplication')
    }
	
    // wire up a different account service if -Duser=something and environment is development
    if (GrailsUtil.environment == "development") {
        switch (System.properties.user) {
            case "testUser1":
                println("Using AutoLoginAccountService - you will be logged in as testUser1")
                accountService(AutoLoginAccountService) {
                    autoAccountName = "testUser1"
					autoAccountDisplayName = "Test User 1"
                    autoRoles = [ERoleAuthority.ROLE_USER.strVal]
                    serviceModelService = ref('serviceModelService')
                    stackService = ref('stackService')
                }
                break
            case "testAdmin1":
                println("Using AutoLoginAccountService - you will be logged in as testAdmin1")
                accountService(AutoLoginAccountService) {
                    autoAccountName = "testAdmin1"
					autoAccountDisplayName = "Test Admin 1"
                    autoRoles = [ERoleAuthority.ROLE_USER.strVal, ERoleAuthority.ROLE_ADMIN.strVal]
                    serviceModelService = ref('serviceModelService')
                    stackService = ref('stackService')
                }
                break
            case "testAdmin2":
                println("Using AutoLoginAccountService - you will be logged in as testAdmin2")
                accountService(AutoLoginAccountService) {
                    autoAccountName = "testAdmin2"
                    autoAccountDisplayName = "Test Admin 2"
                    autoRoles = [ERoleAuthority.ROLE_USER.strVal, ERoleAuthority.ROLE_ADMIN.strVal]
                    serviceModelService = ref('serviceModelService')
                    stackService = ref('stackService')

                }
                break
            default :
                println("You are not using the AutoLoginAccountService. If you want to, add -Duser=[testUser1|testAdmin1|testAdmin2] to your environment.")
                accountService(AccountService) {
                    serviceModelService = ref('serviceModelService')
                    stackService = ref('stackService')
                }
               break
        }
    } else {
        accountService(AccountService) {
            serviceModelService = ref('serviceModelService')
            stackService = ref('stackService')
        }
    }

	exceptionHandler(OwfExceptionResolver)
	{
		exceptionMappings = [
				'java.lang.Exception': '/error'
				]		
	}
}

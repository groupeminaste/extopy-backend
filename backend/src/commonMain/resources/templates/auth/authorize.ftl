<#import "template.ftl" as template>
<@template.form>
    <#if error??>
        <div id="alert-error" class="alert alert-danger" role="alert"><@t key=error /></div>
    </#if>

    <#if client??>
        <h1 class="h3 mb-3 fw-normal"><@t key="auth_authorize_title" args=[client.name] /></h1>

        <div class="mb-4">
            ${client.description}
        </div>

        <button class="w-100 btn btn-lg btn-danger" type="submit"><@t key="auth_field_authorize" /></button>
        <div class="mt-4">
            <@t key="auth_hint_authorize_connected_as" args=["${user.displayName} (@${user.username})"] />
        </div>
        <div class="mt-3">
            <a href="logout?redirect=/account/authorize?clientId=${client.id}" class="text-danger">
                <@t key="auth_hint_authorize_not_you" />
            </a>
        </div>
        <div class="mt-3 small">
            <@t key="auth_hint_authorize_close" />
        </div>
    </#if>
</@template.form>

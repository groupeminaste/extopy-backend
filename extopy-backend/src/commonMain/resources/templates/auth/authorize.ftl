<#import "template.ftl" as template>
<@template.form>
    <h1 class="h3 mb-3 fw-normal">Authorize ${client.name} to access to your account?</h1>

    <#if error??>
        <div class="alert alert-danger" role="alert">${error}</div>
    </#if>

    <div class="mb-4">
        ${client.description}
    </div>

    <button class="w-100 btn btn-lg btn-danger" type="submit">Authorize</button>
    <div class="mt-4">
        Connected as: ${user.displayname} (${user.username})
    </div>
    <div class="mt-3">
        <a href="logout?redirect=/account/authorize?clientId=${client.id}" class="text-danger">Use another account</a>
    </div>
    <div class="mt-3 small">
        If you don't want to authorize this client, just close your browser.
    </div>
</@template.form>
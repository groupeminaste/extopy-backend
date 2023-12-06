<#import "template.ftl" as template>
<@template.form>
    <h1 class="h3 mb-3 fw-normal">Sign in</h1>

    <#if error??>
        <div class="alert alert-danger" role="alert">${error}</div>
    </#if>

    <div class="form-floating">
        <input type="text" class="form-control" id="username" name="username" placeholder="name@example.com">
        <label for="username">Username or email address</label>
    </div>
    <div class="form-floating">
        <input type="password" class="form-control" id="password" name="password" placeholder="Password">
        <label for="password">Password</label>
    </div>

    <div class="checkbox mb-3">
        <label><input type="checkbox" value="remember-me"> Remember me</label>
    </div>
    <button class="w-100 btn btn-lg btn-danger" type="submit">Sign in</button>

    <div class="mt-3">
        <a href="register<#if redirect??>?redirect=${redirect}</#if>" class="text-danger">Sign up</a> instead
    </div>
</@template.form>
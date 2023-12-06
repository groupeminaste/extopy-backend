<#import "template.ftl" as template>
<@template.form>
    <h1 class="h3 mb-3 fw-normal">Sign up</h1>

    <#if error??>
        <div class="alert alert-danger" role="alert">${error}</div>
    </#if>

    <div class="form-floating">
        <input type="text" class="form-control" id="username" name="username" placeholder="example">
        <label for="username">Username</label>
    </div>
    <div class="form-floating">
        <input type="text" class="form-control" id="displayname" name="displayname" placeholder="Example">
        <label for="displayname">Display name</label>
    </div>
    <div class="form-floating">
        <input type="email" class="form-control" id="email" name="email" placeholder="name@example.com">
        <label for="email">Email</label>
    </div>
    <div class="form-floating">
        <input type="password" class="form-control" id="password" name="password" placeholder="Password">
        <label for="password">Password</label>
    </div>
    <div class="form-floating">
        <input type="password" class="form-control" id="password2" name="password2" placeholder="Repeat password">
        <label for="password2">Repeat password</label>
    </div>
    <div class="form-floating">
        <input type="date" class="form-control" id="birthdate" name="birthdate" placeholder="Birthdate">
        <label for="birthdate">Birthdate</label>
    </div>

    <button class="w-100 btn btn-lg btn-danger" type="submit">Sign up</button>

    <div class="mt-3">
        <a href="login<#if redirect??>?redirect=${redirect}</#if>" class="text-danger">Sign in</a> instead
    </div>
</@template.form>
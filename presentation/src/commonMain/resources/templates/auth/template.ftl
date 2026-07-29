<#macro form>
    <!DOCTYPE html>
    <html lang="${locale}">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Authentication</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.2/dist/css/bootstrap.min.css" rel="stylesheet"
              integrity="sha384-Zenh87qX5JnK2Jl0vWa8Ck2rdkQ2Bzep5IDxbcnCeuOxjzrPF/et3URy9Bv1WTRi"
              crossorigin="anonymous">
        <link href="/css/auth.css" rel="stylesheet">
        <#if redirect??>
            <meta http-equiv="refresh" content="0;url=${redirect}">
        </#if>
    </head>
    <body class="text-center">
    <main class="form-signin w-100 m-auto">
        <form method="post">
            <img class="mb-4 logo-rounded" src="/img/logo.png" alt="logo" width="96" height="96">

            <#nested>
        </form>
    </main>
    </body>
    </html>

</#macro>

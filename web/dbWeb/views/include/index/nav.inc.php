<nav class = "navbar navbar-inverse navbar-fixed-top">
    <div class = "container">
        <div class = "navbar-header">
            <button type = "button" class = "navbar-toggle collapsed" data-toggle = "collapse" data-target = "#navbar" aria-expanded = "false" aria-controls = "navbar">
                <span class = "sr-only">Toggle navigation</span>
                <span class = "icon-bar"></span>
                <span class = "icon-bar"></span>
                <span class = "icon-bar"></span>
            </button>
            <a class = "navbar-brand" href = "#">VisUnit</a>
        </div>
        <div id = "navbar" class = "collapse navbar-collapse">
            <ul class = "nav navbar-nav navbar-right">
                <?php if ($isLogged) { ?>
                    <li><a href = "#" onclick="logout()" id="logout">Logout</a></li>

                <?php } else { ?>
                    <li><a href = "#register" data-toggle="modal" data-target="#modalViewRegister">Register</a></li>
                    <li><a href = "#login" data-toggle="modal" data-target="#modalViewLogin">Login</a></li>
                <?php } ?>

            </ul>
        </div>
    </div>
</nav>

<script type="text/javascript">
    function logout() {
        $.ajax({
            type: "POST",
            url: "controllers/User.php",
            data: {
                action: 'logout'
            },
            success: function ()
            {
                location.reload();
            }
        });
    }

</script>
<h2 style="text-align: center;">Login</h2>
<div class="well">
    <form id="login" role="form" >
        <div class="row">
            <div class="form-group">
                <label for="emailLogin">Email address:</label>
                <input name="emailLogin" type="email" class="form-control" id="emailLogin" placeholder="Please enter your email"/>
            </div>
        </div>
        <div class="row">
            <div class="form-group">
                <label for="passwordLogin">Password:</label>
                <input name="passwordLogin"  type="password" class="form-control " id="passwordLogin" placeholder="Please enter a minimum 4 characters password" />
            </div>
        </div>
        <a href="#" id="forgotPassword" onclick="forgotPassword()">Forgot Password?</a>
        <div class="form-group col-sm-offset-5">
            <button id="submitButtonLogin" disabled="disabled" type="submit" class="btn btn-primary">Login</button>
        </div>
    </form>
</div>

<script type="text/javascript">
    $('#submitButtonLogin').click(function (e) {
        e.preventDefault();
        $.ajax({
            type: "POST",
            url: "controllers/User.php",
            data: {
                action: 'login',
                email: $("#emailLogin").val(),
                password: $("#passwordLogin").val()
            },
            success: function ()
            {
                location.reload();
            },
            error: function () {
                alert("Email or Password was not found");
            }
        });
    });
    function forgotPassword() {
        $.ajax({
            type: "POST",
            url: "controllers/User.php",
            data: {
                action: 'forgot',
                email: $("#emailLogin").val()
            },
            success: function ()
            {
                location.reload();
            },
            error: function () {
                alert("Email is not registered");
            }
        });
    }

    $('#passwordLogin, #emailLogin').on('change keydown keyup', function () {
        if ($('#passwordLogin').val().length >= 4 && validateEmail($('#emailLogin').val())) {
            $('#submitButtonLogin').attr('disabled', false);
        } else {
            $('#submitButtonLogin').attr('disabled', true);
        }
    });
    function validateEmail($email) {
        var emailReg = /^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/;
        return emailReg.test($email);
    }
</script>

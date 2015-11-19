<h2 style="text-align: center;">Register User</h2>
<div class="well">
    <form id="register" role="form" >
        <div class="row">
            <div class="form-group">
                <label for="email">Email address:</label>
                <input name="email" type="email" class="form-control" id="email" placeholder="Please enter your email"/>
            </div>
        </div>
        <div class="row">
            <div class="form-group">
                <label for="password">Password:</label>
                <input name="password"  type="password" class="form-control " id="password" placeholder="Please enter a minimum 4 characters password" />
            </div>
        </div>

        <div class="form-group col-sm-offset-5">
            <button id="submitButton" type="submit" class="btn btn-primary" disabled="disabled">Register</button>
        </div>
    </form>
</div>

<script type="text/javascript">
    $('#submitButton').click(function (e) {
        e.preventDefault();
        $.ajax({
            type: "POST",
            url: "controllers/User.php",
            data: {
                action: 'register',
                firstName: $("#firstName").val(),
                lastName: $("#lastName").val(),
                email: $("#email").val(),
                password: $("#password").val(),
            },
            success: function (data)
            {
                if (data === "true") {
                    alert("User successfully created");
                    $('#modalViewRegister').modal('toggle');
                } else {
                    alert("User was not created");
                }
            }
        });
    });

    $('#password, #email').on('change keydown keyup', function () {
        if ($('#password').val().length >= 4 && validateEmail($('#email').val())) {
            $('#submitButton').attr('disabled', false);
        } else {
            $('#submitButton').attr('disabled', true);
        }
    });
    function validateEmail($email) {
        var emailReg = /^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/;
        return emailReg.test($email);
    }
</script>

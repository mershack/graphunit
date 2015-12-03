<h2 style="text-align: center;">User Settings</h2>
<div class="well">
    <form id="changePasswordForm" role="form" >
        <div class="row">
            <div class="form-group">
                <label for="changePassword">Change Password:</label>
                <input name="changePassword" class="form-control" id="changePassword" placeholder="Please enter a minimum 4 characters password" />
            </div>
        </div>

        <div class="form-group col-md-offset-3">
            <button id="submitPasswordButton" type="submit" class="btn btn-primary" disabled="disabled">Change Password</button>
        </div>
        <div class="form-group col-sm-offset-3">
            &nbsp; &nbsp; <button id="deleteAccountButton" type="submit"  class="btn btn-danger">Delete Account</button>
        </div>
    </form>
</div>

<script type="text/javascript">
    var userId = <?php echo $userId ? $userId : 0; ?>;
    $('#deleteAccountButton').click(function () {
        $.ajax({
            type: "POST",
            url: "controllers/User.php",
            data: {
                action: 'delete',
                id: userId
            },
            success: function (data)
            {
                location.reload();
            }
        });
    });

    $('#submitPasswordButton').click(function () {
        $.ajax({
            type: "POST",
            url: "controllers/User.php",
            data: {
                action: 'changePassword',
                id: userId,
                password: $('#changePassword').val()
            },
            success: function (data)
            {
                location.reload();
            }
        });
    });
    $('#changePassword').on('change keydown keyup', function () {
        if ($('#changePassword').val().length >= 4) {
            $('#submitPasswordButton').attr('disabled', false);
        } else {
            $('#submitPasswordButton').attr('disabled', true);
        }
    });
</script>

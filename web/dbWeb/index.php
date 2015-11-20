<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <?php
        include './controllers/Session.php';
        include './views/include/index/head.inc.php';
        include './views/include/index/bootstrap.inc.php';
        ?>

    </head>
    <body>
        <div class="wrapper" style="padding-bottom: 70px;">
            <?php
            include './views/include/index/nav.inc.php';
            ?>
        </div>
        <div class="container">
            <div class="jumbotron">
                <?php
                if (!$isLogged) {
                    include './views/welcome.inc.php';
                } else {
                    include './views/management.inc.php';
                }
                include dirname(__FILE__) . '/views/include/managementTabs/study/addStudy.html';
                ?>
            </div>
        </div>

        <?php
        include dirname(__FILE__) . '/views/include/modalViews.inc.php';
        ?>
    </body>
</html>
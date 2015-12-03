<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <?php
        include dirname(__FILE__) . '/controllers/Session.php';
        include dirname(__FILE__) . '/views/include/index/head.inc.php';
        include dirname(__FILE__) . '/views/include/index/bootstrap.inc.php';
        ?>

    </head>
    <body>
        <div class="wrapper" style="padding-bottom: 70px;">
            <?php
            include  dirname(__FILE__) . '/views/include/index/nav.inc.php';
            ?>
        </div>
        <div class="container">
            <div class="jumbotron">
                <?php
                if (!$isLogged) {
                    include dirname(__FILE__) . '/views/welcome.inc.php';
                } else {
                    include dirname(__FILE__) . '/views/management.inc.php';
                }
//                include dirname(__FILE__) . '/views/include/managementTabs/study/addStudy.html';
                ?>
            </div>
        </div>

        <?php
        include dirname(__FILE__) . '/views/include/modalViews.inc.php';
        ?>
    </body>
</html>
<div id = "modalViewRegister" class = "modal fade" role = "dialog" style = "padding-top: 100px;">
    <div class = "jumbotron col-sm-offset-5 col-md-3" style = "border-radius: 6px;">
        <?php
        include (dirname(__FILE__) . '/user/register.inc.php');
        ?>
    </div>
</div>
<div id = "modalViewLogin" class = "modal fade" role = "dialog" style = "padding-top: 100px;">
    <div class = "jumbotron col-sm-offset-5 col-md-3" style = "border-radius: 6px;">
        <?php
        include (dirname(__FILE__) . '/user/login.inc.php');
        ?>
    </div>
</div>
<div id = "modalAddStudy" class = "modal fade" role = "dialog" style = "padding-top: 100px;">
    <div class = "jumbotron col-sm-offset-5 col-md-3" style = "border-radius: 6px;">
        <?php
        include (dirname(__FILE__) . '/managementTabs/study/addStudy.html');
        ?>
    </div>
</div>
<div id = "modalAddViewer" class = "modal fade" role = "dialog" style = "padding-top: 100px;">
    <div class = "jumbotron col-sm-offset-5 col-md-3" style = "border-radius: 6px;">
        <?php
        include (dirname(__FILE__) . '/managementTabs/viewer/addViewer.html');
        ?>
    </div>
</div>
<div id = "modalAddDataset" class = "modal fade" role = "dialog" style = "padding-top: 100px;">
    <div class = "jumbotron col-sm-offset-5 col-md-3" style = "border-radius: 6px;">
        <?php
        include (dirname(__FILE__) . '/managementTabs/dataset/addDataset.html');
        ?>
    </div>
</div>
<div id = "modalAddTask" class = "modal fade" role = "dialog" style = "padding-top: 100px;">
    <div class = "jumbotron col-sm-offset-5 col-md-3" style = "border-radius: 6px;">
        <?php
        include (dirname(__FILE__) . '/managementTabs/dataset/addTask.html');
        ?>
    </div>
</div>
<div id = "editDataset" class = "modal fade" role = "dialog" style = "padding-top: 100px;">
    <div class = "jumbotron col-sm-offset-5 col-md-3" style = "border-radius: 6px;">
        <?php
        include (dirname(__FILE__) . '/managementTabs/dataset/editDataset.html');
        ?>
    </div>
</div>
<div id = "editViewer" class = "modal fade" role = "dialog" style = "padding-top: 100px;">
    <div class = "jumbotron col-sm-offset-5 col-md-3" style = "border-radius: 6px;">
        <?php
        include (dirname(__FILE__) . '/managementTabs/viewer/editViewer.html');
        ?>
    </div>
</div>
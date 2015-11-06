<style>
    .table td:nth-child(2){
        width: 80%;
    }
</style>
<ul class="nav nav-tabs">
    <li class="active"><a  href="#studyManagementTab" data-toggle="tab">Study</a></li>
    <li><a href="#datasetManagementTab" data-toggle="tab">Dataset</a></li>
    <li><a href="#viewerManagementTab" data-toggle="tab">Viewer</a></li>
</ul>

<div class="well">
    <div class="tab-content">
        <div class="tab-pane active" id="studyManagementTab">
            <?php
            include './views/include/managementTabs/study/studyManagement.php';
            ?>
        </div>
        <div class="tab-pane" id="datasetManagementTab" >
            <?php
            include './views/include/managementTabs/dataset/datasetManagement.inc.php';
            ?>
        </div>
        <div class="tab-pane" id="viewerManagementTab" >
            <?php
            include './views/include/managementTabs/viewer/viewerManagement.inc.php';
            ?>
        </div>
    </div>
</div>

<script type="text/javascript">
    $(document).ready(function () {
        var lastTab = $.cookie('last_tab');

// if last seen tab was stored in cookie
        if (typeof (lastTab) !== "undefined") {
//remove active css class from all the unordered list items
            $('ul.nav-tabs').children().removeClass('active');
            $('a[href=' + lastTab + ']').parents('li:first').addClass('active');
            $('div.tab-content').children().removeClass('active');
            $(lastTab).addClass('active');
        }
    });

// event to capture tab switch
    $('a[data-toggle="tab"]').on('shown.bs.tab', function (event) {
        event.preventDefault();
//save the latest tab using a cookie:
        $.cookie('last_tab', $(event.target).attr('href'));
    });
</script>


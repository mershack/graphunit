<?php
require './controllers/Dataset.php';
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$dataset = new Dataset(Dataset::$tableName, $userId);
$result = $dataset->datasetManagerView();
?>
<div class="container">
    <div class="row">
        <div class="col-md-offset-4">
            <div class="col-md-6">
                <h2>Dataset Management</h2>
            </div>
            <div class="col-md-3">
            </div>
            <button id="addDatasetButton" onclick="getDataset(); setDatasetid(<?php echo ($result[0]['id'] + 1); ?>);" class="btn btn-primary text-right" data-toggle="modal" 
                    data-target="#modalAddDataset" >Add Dataset</button>
        </div>
    </div>

    <div class="container">
        <table class="table table-bordered table-hover">
            <thead>
                <tr>     
                    <th>ID</th>
                    <th>Name</th>
                    <th>Action</th>           
                </tr>
            </thead>
            <?php
            foreach ($result as $value) {
                ?>

                <tr>
                    <td>
                        <?php
                        echo $value['id'];
                        ?>
                    </td>
                    <td>
                        <?php
                        echo $value['name'];
                        ?>
                    </td>
                    <td class="text-right">
                        <button type="button" class="btn btn-default btn-xs" onclick="copyDataset('<?php echo $value['name']; ?>', '<?php echo $value['path']; ?>', '<?php echo $value['description']; ?>')">Copy</button>
                        <button type="button" onclick="setDatasetid(<?php echo $value['id']; ?>); getDataset();" class="btn btn-default btn-xs" id="editDatasetOpen_<?php echo $value['id']; ?>" data-toggle="modal" 
                                data-target="#editDataset">Edit</button>
                        <button type="button" class="btn btn-danger btn-xs" onclick="delDataset(<?php echo $value['id']; ?>)">Delete</button>
                    </td>
                </tr>
                <?php
            }
            ?>
        </table>
    </div>
</div>

<script type="text/javascript">
    var datasetId;
    var userId = <?php echo $userId; ?>;
    function setDatasetid(id){
        datasetId = id;
    }
    function copyDataset(name, path, description) {
        $.ajax({
            type: "POST",
            url: "controllers/Dataset.php",
            data: {
                action: 'copy',
                userId: userId,
                name: name,
                path: path,
                description: description
            },
            success: function (data)
            {
                if (data === "true") {
                    location.reload();
                } else {
                    alert("There was an error, please try again.");
                }
            }
        });

    }
    function delDataset(id) {
        $.ajax({
            type: "POST",
            url: "controllers/Dataset.php",
            data: {
                action: 'delete',
                userId: userId,
                id: id
            },
            success: function (data)
            {
                if (data === "true") {
                    location.reload();
                } else {
                    alert("There was an error, please try again.");
                }
            }
        });
    }
</script>

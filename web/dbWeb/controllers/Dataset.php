<?php

require_once '/DatabaseManager.php';

require_once (dirname(__FILE__) .'/Session.php');

class Dataset extends DatabaseManager {
    
    /*
     * Current user id
     * @var type int
     */
    private $userId;

    /**
     * Name of the table
     * @var type string
     */
    public static $tableName = 'dataset';

    /**
     * Array that holds columnds in a table.
     * Update when a database is changed
     * @var type Array
     */
    private $fields = ['name', 'path', 'user_id', 'description'];

    public function __construct($tableName, $userId) {
        parent::__construct($tableName);
        $this->userId = $userId;
    }

    /*
     * Get all Datasets to populate list manager
     */

    public function datasetManagerView() {
        $fields = ['user_id'];
        $conditions = [$this->userId];
        $order = ['id DESC'];
        return $this->select($fields, $conditions, $order);
    }

    /*
     * Delete an existing dataset
     */

    public function del() {
        $successful = $this->delete($_POST['id']);

        $result = true;

        if (!$successful) {
            $result = false;
        }

        echo json_encode($result);
    }

    /*
     * Edit an existing dataset
     */

    public function edit() {
        $data = [$_POST['name'], null, $this->userId, isset($_POST['description']) ? $_POST['description'] : null];
        $successful = $this->update($_POST['datasetId'], $this->fields, $data);

        $result = true;

        if (!$successful) {
            $result = false;
        }

        echo json_encode($result);
    }
    
    /*
     * Retrieve information to edit dataset
     */
    public function editInfo() {
        $fields = ['id'];
        $conditions = [$_POST['datasetId']];
        $dataset = $this->select($fields, $conditions);

        $data = array();
        $data['name'] = $dataset[0]['name'];
        $data['description'] = $dataset[0]['description'];
        echo json_encode($data);
    }

    /*
     * Copy an existing dataset
     */

    public function copy() {
        $data = [$_POST['name'], null, $this->userId, $_POST['description'] ? $_POST['description'] : null ];
        $successful = $this->insert($this->fields, $data);

        $result = true;

        if (!$successful) {
            $result = false;
        }

        echo json_encode($result);
    }
    /*
     * Add a dataset
     */

    public function add() {
        $data = [$_POST['name'], null, $this->userId, isset($_POST['description']) ? $_POST['description'] : null];
        $successful = $this->insert($this->fields, $data);

        $result = true;

        if (!$successful) {
            $result = false;
        }

        echo json_encode("asdasd");
    }

}
if(isset($_POST['action'])) {
    $dataset = new Dataset(Dataset::$tableName, $_POST['userId']);
    switch ($_POST['action']) {
        case 'delete':
            $dataset->del();
            break;
        case 'copy':
            $dataset->copy();
            break;
        case 'edit':
            $dataset->edit();
            break;
        case 'editInfo':
            $dataset->editInfo();
            break;
        case 'add':
            $dataset->add();
            break;
    }
}
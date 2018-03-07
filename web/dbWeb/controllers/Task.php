<?php

require_once '/DatabaseManager.php';

require_once '/Session.php';

class Task extends DatabaseManager {
    /*
     * Current user id
     * @var type int
     */

    private $userId;

    /**
     * Name of the table
     * @var type string
     */
    public static $tableName = 'task';

    /**
     * Array that holds columnds in a table.
     * Update when a database is changed
     * @var type Array
     */
    private $fields = ['name', 'path', 'dataset_id', 'description'];

    public function __construct($tableName, $userId) {
        parent::__construct($tableName);
        $this->userId = $userId;
    }

    /*
     * Add a dataset
     */

    public function add() {
        $data = [$_POST['name'], null, $_POST['datasetId'], isset($_POST['description']) ? $_POST['description'] : null];
        $successful = $this->insert($this->fields, $data);
        if ($successful) {
            $result = $this->select();
        }
        echo json_encode(intval($result[count($result)-1]['id']));
    }

}

if (isset($_POST['action'])) {
    $viewer = new Task(Task::$tableName, $_POST['userId']);
    switch ($_POST['action']) {
        case 'add':
            $viewer->add();
            break;
    }
}
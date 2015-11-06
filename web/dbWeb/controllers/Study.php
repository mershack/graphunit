<?php

require_once '/DatabaseManager.php';

require_once '/Session.php';

class Study extends DatabaseManager {
    
    /*
     * Current user id
     * @var type int
     */
    private $userId;

    /**
     * Name of the table
     * @var type string
     */
    public static $tableName = 'study';

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
     * Get all studies to populate list manager
     */

    public function studyManagerView() {
        $fields = ['user_id'];
        $conditions = [$this->userId];
        $order = ['id DESC'];
        return $this->select($fields, $conditions, $order);
    }

    /*
     * Delete an existing study
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
     * Edit an existing Study
     */

    public function edit() {
        $successful = $this->delete($_POST['id']);

        $result = true;

        if (!$successful) {
            $result = false;
        }

        echo json_encode($result);
    }

    /*
     * Copy an existing study
     */

    public function copy() {
        $data = [$_POST['name'], $_POST['path'], $this->userId];
        $successful = $this->insert($this->fields, $data);

        $result = true;

        if (!$successful) {
            $result = false;
        }

        echo json_encode($result);
    }

}

if(isset($_POST['action'])) {
    $study = new Study(Study::$tableName, $_POST['userId']);
    switch ($_POST['action']) {
        case 'delete':
            $study->del();
            break;
        case 'copy':
            $study->copy();
            break;
        case 'edit':
            $study->edit();
            break;
    }
}
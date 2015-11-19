<?php

require_once '/DatabaseManager.php';

require_once '/Session.php';

class Viewer extends DatabaseManager {
    /*
     * Current user id
     * @var type int
     */

    private $userId;

    /**
     * Name of the table
     * @var type string
     */
    public static $tableName = 'viewer';

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
     * Get all viewers to populate list manager
     */

    public function viewerManagerView() {
        $fields = ['user_id'];
        $conditions = [$this->userId];
        $order = ['id DESC'];
        return $this->select($fields, $conditions, $order);
    }

    /*
     * Delete an existing viewer
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
     * Edit an existing viewer
     */

    public function edit() {
        $data = [$_POST['name'], null, $this->userId, isset($_POST['description']) ? $_POST['description'] : null];
        $successful = $this->update($_POST['viewerId'], $this->fields, $data);

        $result = true;

        if (!$successful) {
            $result = false;
        }

        echo json_encode($result);
    }

    /*
     * Retrieve information to edit viewer
     */

    public function editInfo() {
        $fields = ['id'];
        $conditions = [$_POST['viewerId']];
        $dataset = $this->select($fields, $conditions);

        $data = array();
        $data['name'] = $dataset[0]['name'];
        $data['description'] = $dataset[0]['description'];
        echo json_encode($data);
    }

    /*
     * Copy an existing viewer
     */

    public function copy() {
        $data = [$_POST['name'], null, $this->userId, $_POST['description'] ? $_POST['description'] : null];
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
        if ($successful) {
            $result = $this->select();
        }
        echo json_encode(intval($result[count($result)-1]['id']));
    }

}

if (isset($_POST['action'])) {
    $viewer = new Viewer(Viewer::$tableName, $_POST['userId']);
    switch ($_POST['action']) {
        case 'delete':
            $viewer->del();
            break;
        case 'copy':
            $viewer->copy();
            break;
        case 'editViewer':
            $viewer->edit();
            break;
        case 'editInfo':
            $viewer->editInfo();
            break;
        case 'add':
            $viewer->add();
            break;
    }
}
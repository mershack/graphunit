<?php

require_once '/config.php';
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

class DatabaseManager {

    private $mysqli;
    private $tableName;

    protected function __construct($tableName) {
        $this->tableName = $tableName;
        $this->mysqli = new mysqli(DB_SERVER, DB_USER, DB_PASSWORD, DB_SCHEMA_NAME);
        if ($this->mysqli->connect_errno) {
            printf("Connect failed: %s\n", $this->mysqli->connect_error);
            exit();
        }
    }

    public function __destruct() {
        $this->mysqli->close();
    }

    /**
     * Create a new item in the database
     * 
     * @param type $fields Fields in the same order as the database
     * @param type $data Data to be inserted
     * @return type If false, insert was unsuccessful
     */
    protected function insert($fields = array(), $data = array()) {
        if (empty($fields) || empty($data)) {
            die("SQL can not insert without data, fields or table.");
        }

        $columns = null;
        $columnsData = null;
        foreach ($fields as $key => $current) {
            if ($key == 0) {
                $columns = $current;
                $columnsData = $data[$key];
                continue;
            }


            if (!is_null($data[$key])) {
                $columns = $columns . "`, `" . $current;
                $columnsData = $columnsData . "', '" . $data[$key];
            }
        }

        $sqlQuery = "INSERT INTO `" . $this->tableName . "` (`" . $columns . "`) VALUES ('" . $columnsData . "')";

        return $this->mysqli->query($sqlQuery);
    }

    /**
     * Delete an existing item in the database
     * 
     * @param type $id int in the same order as the database
     * @return type If false, insert was unsuccessful
     */
    protected function delete($id) {
        if (is_null($id)) {
            return false;
        }


        $sqlQuery = "DELETE FROM `" . $this->tableName . "`WHERE `id` = " . $id;

        return $this->mysqli->query($sqlQuery);
    }
    /**
     * Update an existing item in the database
     * 
     * @param type $id int in the same order as the database
     * @param type $fields Fields in same order as condition
     * @param type $data Data to be inserted
     * @return type If false, insert was unsuccessful
     */
    protected function update($id, $fields = array(), $data = array()) {
        if (is_null($id)) {
            return false;
        }

        $update = "`id`  = '".$id."' ";
        foreach ($fields as $key => $current) {
            if (!is_null($data[$key])) {
                $update = $update . ", `".$current."` = '".$data[$key]."' ";
            }
        }

        $sqlQuery = "UPDATE `" . $this->tableName . "` SET ".$update." WHERE `id` = ".$id;

        return $this->mysqli->query($sqlQuery);
    }

    /**
     * Return one or multiple items from the database
     * 
     * @param type $fields Fields in same order as condition
     * @param type $conditions 0 or more conditions for the select
     * @param type $order Order result by field/s.
     * @return type array 
     */
    protected function select($fields = array(), $conditions = array(), $order = array()) {
        $where = null;

        if (!empty($fields) && !empty($conditions)) {
            foreach ($fields as $key => $current) {
                if ($key == 0) {
                    $where = " WHERE `" . $current . "` = '" . $conditions[$key] . "'";
                    continue;
                }

                $where = $where . " AND `" . $current . "` = '" . $conditions[$key] . "'";
            }
        }
        
        $orderBy = null;
        
        if (!empty($order)) {
            foreach ($order as $key => $current) {
                if ($key == 0) {
                    $orderBy = " ORDER BY " . $current . "";
                    continue;
                }
                $orderBy = $orderBy . ", `" . $current . "`";
            }
        }
        $sqlQuery = "SELECT * FROM `" . $this->tableName . "`" . $where . "" . $orderBy;

        if ($result = $this->mysqli->query($sqlQuery)) {
            $arrayResult = array();
            /* fetch object array */
            while ($array = $result->fetch_array()) {
                $arrayResult[] = $array;
            }
        }
        return $arrayResult;
    }

}

?>

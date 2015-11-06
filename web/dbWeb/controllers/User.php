<?php

require_once './DatabaseManager.php';
require '../phpmailer/PHPMailerAutoload.php';
require '../phpmailer/class.smtp.php';

class User extends DatabaseManager {

    /**
     * Name of the table
     * @var type string
     */
    public static $tableName = 'user';

    /**
     * Array that holds columnds in a table.
     * Update when a database is changed
     * @var type Array
     */
    private $fields = ['email', 'password', 'amazon_turk_id'];

    public function __construct($tableName) {
        parent::__construct($tableName);
    }

    /**
     * Process of registering a user 
     * using the "insert()" function 
     * in DatabaseManager
     */
    public function register() {
        $data = [$_POST['email'], $_POST['password'], null];

        $successful = $this->insert($this->fields, $data);

        $result = true;

        if (!$successful) {
            $result = false;
        }

        echo json_encode($result);
    }

    /**
     * Process of sending a password
     * to a specified email
     */
    public function forgotPassword() {
        $fields = ['email'];
        $conditions = [$_POST['email']];
        $result = $this->select($fields, $conditions);
        if (!empty($result)) {
            $mail = new PHPMailer();
            $mail->Host = 'localhost';
            $mail->From = "noreply@visunit.com";
            $mail->FromName = "VisUnit Password Recovery";
            $mail->Subject = "Here is your password";
            $mail->MsgHTML("Prueba");

            $mail->isSMTP();                                      // Set mailer to use SMTP
            $mail->Host = 'localhost';  // Specify main and backup SMTP servers
            $mail->SMTPAuth = true;                               // Enable SMTP authentication
            $mail->Username = 'user@example.com';                 // SMTP username
            $mail->Password = 'secret';                           // SMTP password
            $mail->SMTPSecure = 'tls';                            // Enable TLS encryption, `ssl` also accepted
            $mail->Port = 587;                                    // TCP port to connect to

            $mail->setFrom('noreply@visunit.com', 'Mailer');
            $mail->addAddress($_POST['email']);

            $mail->isHTML(true);                                  // Set email format to HTML

            $mail->Subject = 'Here is your password!';
            $mail->Body = 'This is the HTML message body <b>in bold!</b>';

            if ($mail->Send()) {
                echo json_encode(true);
            } else {
                echo json_encode(false);
                echo 'Mailer Error: ' . $mail->ErrorInfo;
            }
        } else {
            echo json_encode(false);
        }
    }

    /**
     * Process of loging in a user 
     * using the "select()" function 
     * in DatabaseManager
     */
    public function login() {
        $fields = ['email', 'password'];
        $conditions = [$_POST['email'], $_POST['password']];
        $result = $this->select($fields, $conditions);
        if (!empty($result)) {
            session_start();
            $_SESSION['userId'] = $result['0']['id'];
            $_SESSION['firstName'] = $result['0']['firstName'];
            $_SESSION['lastName'] = $result['0']['lastName'];
            $_SESSION['email'] = $result['0']['email'];
            echo json_encode(true);
            return;
        }

        echo json_encode(false);
    }

    /**
     * Process of loging out a user 
     * using the session_destroy()
     */
    public function logout() {
        session_start();
        session_destroy();
        $_SESSION = array();
    }

}

$user = new User(User::$tableName);
switch ($_POST['action']) {
    case 'register':
        $user->register();
        break;
    case 'login':
        $user->login();
        break;
    case 'logout':
        $user->logout();
        break;
    case 'forgot':
        $user->forgotPassword();
        break;
}
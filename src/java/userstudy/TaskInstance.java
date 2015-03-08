/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userstudy;

/**
 *
 * @author Mershack
 */
public class TaskInstance {

    private String nodes;
    private String answer;

    public TaskInstance() {

    }

    public TaskInstance(String nodes, String answer) {
        this.nodes = nodes;
        this.answer = answer;
    }

    public String getNodes() {
        return nodes;
    }

    public void setNodes(String nodes) {
        this.nodes = nodes;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "nodes: " + this.nodes + "  "
                + "answer: " + this.answer + "  ";
    }
}

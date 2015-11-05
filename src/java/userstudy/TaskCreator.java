/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userstudy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Mershack
 */
@WebServlet(name = "TaskCreator", urlPatterns = {"/TaskCreator"})
public class TaskCreator extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();

        try {
            String command = request.getParameter("command");

            if (command.equalsIgnoreCase("checkTaskNameAvailability")) {
                //read the quanttask file to see if the name exists
                String taskShortName = request.getParameter("taskShortName").toString();

                String msg = "notExists";

                //read the quant-task-files.
                String filename = getServletContext().getRealPath("quanttasks" + File.separator + "quanttasks.xml");
                File fXmlFile = new File(filename);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(fXmlFile);
                doc.getDocumentElement().normalize();

                //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
                NodeList taskNode = doc.getElementsByTagName("task");
                //get the condition urls and shortnames
                for (int i = 0; i < taskNode.getLength(); i++) {
                    Node nNode = taskNode.item(i);

                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        String tname = eElement.getElementsByTagName("taskname").item(0).getTextContent();

                        if (taskShortName.trim().equalsIgnoreCase(tname)) {
                            msg = "exists";
                            break;
                        }

                    }
                }
                out.write(msg);

            } else if (command.equalsIgnoreCase("saveNewTask")) {
                //First Get the parameters of the tasks
                String taskQuestion = request.getParameter("taskQuestion").toString();
                String taskShortName = request.getParameter("taskShortName").toString();
                String answerType = request.getParameter("answerType").toString();
                String inputInterface = request.getParameter("inputInterface").toString();
                String outputInterface = request.getParameter("outputInterface").toString();
                String numberOfInputs = request.getParameter("numberOfInputs").toString();
                String answerOptions = request.getParameter("answerOptions").toString();
                String inputTypeShortNames = request.getParameter("inputTypeShortNames").toString();
                String inputTypeDescriptions = request.getParameter("inputTypeDescriptions").toString();
                                                             

                String inputTypeShortNamesArr[] = inputTypeShortNames.split(":::");
                String inputTypeDescriptionsArr[] = inputTypeDescriptions.split(":::");
                
                
                
                
                
                //conjugate the answertype
                if(answerType.equalsIgnoreCase("Selection")){
                    answerType = "interface:::"+answerType + ":::"+answerOptions;
                }
                else{
                    answerType = "widget:::"+answerType + ":::"+answerOptions;
                }
                
                //Write an xml file with it.
                String quanTaskFileName = getServletContext().getRealPath("quanttasks" + File.separator + "quanttasks.xml");

                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document doc = documentBuilder.parse(quanTaskFileName);
                //Get the root element
                Node root = doc.getFirstChild();

                //create the task element
                Element taskElem = doc.createElement("task");
                //create the elements that will be below the task element
                Element tasknameElem = doc.createElement("taskname");
                Element inpInterfaceElem = doc.createElement("inputinterface");
                Element outInterfaceElem = doc.createElement("outputinterface");
                Element answertypeElem = doc.createElement("answertype");
                Element taskquestionElem = doc.createElement("taskquestion");
                Element inputsizeElem = doc.createElement("inputsize");

                tasknameElem.appendChild(doc.createTextNode(taskShortName));
                taskquestionElem.appendChild(doc.createTextNode(taskQuestion));
                answertypeElem.appendChild(doc.createTextNode(answerType));
                inpInterfaceElem.appendChild(doc.createTextNode(inputInterface));
                outInterfaceElem.appendChild(doc.createTextNode(outputInterface));
                inputsizeElem.appendChild(doc.createTextNode(numberOfInputs));
              





                //append the elements to the task element
                taskElem.appendChild(tasknameElem);
                taskElem.appendChild(inpInterfaceElem);
                taskElem.appendChild(outInterfaceElem);
                taskElem.appendChild(answertypeElem);
                taskElem.appendChild(taskquestionElem);
                taskElem.appendChild(inputsizeElem);

                
                //create the input elements
                for(int i=0; i<Integer.parseInt(numberOfInputs); i++){
                    Element inputElem = doc.createElement("input");
                    
                    Element inputShortNameElem = doc.createElement("inputtype");
                    inputShortNameElem.appendChild(doc.createTextNode(inputTypeShortNamesArr[i]));
                    
                    Element inputDescriptionElem = doc.createElement("inputdescription");
                    inputDescriptionElem.appendChild(doc.createTextNode(inputTypeDescriptionsArr[i]));
                    
                    inputElem.appendChild(inputShortNameElem);
                    inputElem.appendChild(inputDescriptionElem);
                    
                    //append the input Elem to the task Elem.
                    taskElem.appendChild(inputElem);                    
                }
                
                
                
                
                
                
                
                //append the task node to the root node.
                root.appendChild(taskElem);
                
                //write it to file
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File(quanTaskFileName));                
                transformer.transform(source, result);
                
              //  System.out.println("It is done now. Everything is done");
               // out.print("it's done");
            }
        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

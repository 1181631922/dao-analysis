package com.rili.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.SynchronizedStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.rili.Bean.InsertTableBean;
import com.rili.dao.SlaveDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Author： fanyafeng
 * Data： 17/3/14 10:23
 * Email: fanyafeng@live.cn
 */
@Service
public class DAOTableSv {

    @Autowired
    private SlaveDAO slaveDAO;

    private static final Logger LOGGER = LoggerFactory.getLogger(DAOTableSv.class);
    private static String INSERT = "insert";
    private static String SELECT = "select";
    private static String UPDATE = "update";
    private static String DELETE = "delete";

    private Set<InsertTableBean> insertTableBeanSet = new TreeSet<>();

    public void test(String fullFilePath) {
        if (fullFilePath.contains("/")) {
            insertTableBeanSet.clear();
            int fileNameLength = fullFilePath.split("/").length;
            String fileName = Arrays.asList(fullFilePath.split("/")).get(fileNameLength - 1);
            try {
                FileInputStream fileInputStream = new FileInputStream(fullFilePath);
                CompilationUnit compilationUnit = JavaParser.parse(fileInputStream);
                new MethodChangerVisitor(fileName.replace(".java", ""), insertTableBeanSet).visit(compilationUnit, null);

                System.out.println(insertTableBeanSet.size());
                List<InsertTableBean> insertTableBeanList = new ArrayList<>(insertTableBeanSet);
                for (InsertTableBean insertTableBean : insertTableBeanList) {
                    slaveDAO.insertTable(insertTableBean);
                }
            } catch (FileNotFoundException e) {
                LOGGER.error("FileNotFoundException:{}", e);
            }
        }
    }

    private class MethodChangerVisitor extends VoidVisitorAdapter<Void> {

        private String fileName;
        private Set<InsertTableBean> insertTableBeanSet;

        public MethodChangerVisitor(String fileName, Set<InsertTableBean> insertTableBeanSet) {
            this.fileName = fileName;
            this.insertTableBeanSet = insertTableBeanSet;
        }

        @Override
        public void visit(MethodDeclaration n, Void arg) {
//            System.out.println("---" + n.getNameAsString());
            String methodName = n.getNameAsString();
            List<String> stringList = Arrays.asList(n.getBody().toString().split(";"));
            for (int i = 0; i < stringList.size(); i++) {
                String node = stringList.get(i).replace("\" + \"", "").replace("\" +", "").replace("+ \"", "").toLowerCase();
                if (node.contains("insert into")) {//增
//                    System.out.println(node);
                    int simpleStart = node.indexOf("into ") + 5;
                    int simpleEnd = node.indexOf(",");
                    String simpleTable = node.substring(simpleStart, simpleEnd);
                    if (simpleTable.contains(" ")) {
                        String currentTable = simpleTable.substring(0, simpleTable.indexOf(" "));
                        System.out.println("文件名:" + fileName + ";方法名:" + methodName + ";数据库表名:" + currentTable + ";操作:insert");
                        insertTableBeanSet.add(new InsertTableBean(fileName, methodName, currentTable, INSERT));
                    } else if (simpleTable.contains("(")) {
                        String currentTable = simpleTable.substring(0, simpleTable.indexOf("("));
                        System.out.println("文件名:" + fileName + ";方法名:" + methodName + ";数据库表名:" + currentTable + ";操作:insert");
                        insertTableBeanSet.add(new InsertTableBean(fileName, methodName, currentTable, INSERT));
                    }
                } else if (node.contains("update ")) {//改
                    String currentTable = node.substring(node.indexOf("update ") + 6, node.indexOf("set") - 1).trim();
                    System.out.println("文件名:" + fileName + ";方法名:" + methodName + ";数据库表名:" + currentTable + ";操作:update");
                    insertTableBeanSet.add(new InsertTableBean(fileName, methodName, currentTable, UPDATE));
                } else if (node.contains("delete from ")) {//删
                    String currentTable = node.substring(node.indexOf("delete from ") + 12, node.indexOf("where"));
                    System.out.println("文件名:" + fileName + ";方法名:" + methodName + ";数据库表名:" + currentTable + ";操作:delete");
                    insertTableBeanSet.add(new InsertTableBean(fileName, methodName, currentTable, DELETE));
                } else if (node.contains("select") && node.contains("where")) {//有where的查
                    List<String> simpleTableList = Arrays.asList(node.split("from "));
                    for (int j = 0; j < simpleTableList.size(); j++) {
                        if (j > 0) {
                            String simpleTable = simpleTableList.get(j).trim();
                            if (!simpleTable.contains("select")) {
                                if (j == simpleTableList.size() - 1 && simpleTable.contains(")") && !simpleTable.contains(" ")) {
                                    System.out.println("文件名:" + fileName + ";方法名:" + methodName + ";数据库表名:" + simpleTable.substring(0, simpleTable.indexOf(")")) + ";操作:select");
                                    insertTableBeanSet.add(new InsertTableBean(fileName, methodName, simpleTable.substring(0, simpleTable.indexOf(")")), SELECT));
                                } else {
                                    System.out.println("文件名:" + fileName + ";方法名:" + methodName + ";数据库表名:" + simpleTable.substring(0, simpleTable.indexOf(" ")).replace("(", "") + ";操作:select");
                                    insertTableBeanSet.add(new InsertTableBean(fileName, methodName, simpleTable.substring(0, simpleTable.indexOf(" ")).replace("(", ""), SELECT));
                                }
                            }

                            List<String> joniStringList = Arrays.asList(simpleTable.split("join "));
                            for (int k = 0; k < joniStringList.size(); k++) {
                                if (k > 0) {
                                    String simpleJoinTable = joniStringList.get(k).trim();
                                    if (k == joniStringList.size() - 1 && simpleJoinTable.contains(")") && !simpleJoinTable.contains(" ")) {
                                        String selectGroupTable = simpleJoinTable.substring(0, simpleJoinTable.indexOf(")"));
                                        if (selectGroupTable.contains(",")) {
                                            List<String> groupList = Arrays.asList(selectGroupTable.split(","));
                                            for (int h = 0; h < groupList.size(); h++) {
                                                String selectItemTable = groupList.get(h);
                                                System.out.println("文件名:" + fileName + ";方法名:" + methodName + ";数据库表名:" + selectItemTable + ";操作:select");
                                                insertTableBeanSet.add(new InsertTableBean(fileName, methodName, selectItemTable, SELECT));
                                            }
                                        } else {
                                            System.out.println("文件名:" + fileName + ";方法名:" + methodName + ";数据库表名:" + selectGroupTable + ";操作:select");
                                            insertTableBeanSet.add(new InsertTableBean(fileName, methodName, selectGroupTable, SELECT));
                                        }
                                    } else {
                                        String selectGroupTable = simpleJoinTable.substring(0, simpleJoinTable.indexOf(" "));
                                        if (selectGroupTable.contains(",")) {
                                            List<String> groupList = Arrays.asList(selectGroupTable.split(","));
                                            for (int h = 0; h < groupList.size(); h++) {
                                                String selectItemTable = groupList.get(h);
                                                System.out.println("文件名:" + fileName + ";方法名:" + methodName + ";数据库表名:" + selectItemTable + ";操作:select");
                                                insertTableBeanSet.add(new InsertTableBean(fileName, methodName, selectItemTable, SELECT));
                                            }
                                        } else {
                                            System.out.println("文件名:" + fileName + ";方法名:" + methodName + ";数据库表名:" + selectGroupTable + ";操作:select");
                                            insertTableBeanSet.add(new InsertTableBean(fileName, methodName, selectGroupTable, SELECT));
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (node.contains("select") && !node.contains("where")) {//不包含where的查
//                    System.out.println(node);
                    String sqlString = node.substring(node.indexOf("from ") + 5, node.length()).trim();
                    if (sqlString.contains("\"")) {
                        String simpleTable = sqlString.substring(0, sqlString.indexOf("\"")).trim();
                        if (simpleTable.contains(" ")) {
                            String currentTable = simpleTable.substring(0, simpleTable.indexOf(" "));
                            if (currentTable.contains(",")) {
                                List<String> currentTableList = Arrays.asList(currentTable.split(","));
                                for (int h = 0; h < currentTableList.size(); h++) {
                                    System.out.println("文件名:" + fileName + ";方法名:" + methodName + ";数据库表名:" + currentTableList.get(h) + ";操作:select");
                                    insertTableBeanSet.add(new InsertTableBean(fileName, methodName, currentTableList.get(h), SELECT));
                                }
                            } else {
                                System.out.println("文件名:" + fileName + ";方法名:" + methodName + ";数据库表名:" + currentTable + ";操作:select");
                                insertTableBeanSet.add(new InsertTableBean(fileName, methodName, currentTable, SELECT));
                            }
                        } else {
                            if (simpleTable.contains(",")) {
                                List<String> simpleTableList = Arrays.asList(simpleTable.split(","));
                                for (int h = 0; h < simpleTableList.size(); h++) {
                                    System.out.println("文件名:" + fileName + ";方法名:" + methodName + ";数据库表名:" + simpleTableList.get(h) + ";操作:select");
                                    insertTableBeanSet.add(new InsertTableBean(fileName, methodName, simpleTableList.get(h), SELECT));
                                }
                            } else {
                                System.out.println("文件名:" + fileName + ";方法名:" + methodName + ";数据库表名:" + simpleTable + ";操作:select");
                                insertTableBeanSet.add(new InsertTableBean(fileName, methodName, simpleTable, SELECT));
                            }
                        }
                    } else {
                        String sqlTrimString = sqlString.trim();
                        if (sqlTrimString.contains(" ")) {
                            String currentString = sqlTrimString.substring(0, sqlTrimString.indexOf(" "));
                            if (currentString.contains(",")) {
                                List<String> simpleTableList = Arrays.asList(currentString.split(","));
                                for (int h = 0; h < simpleTableList.size(); h++) {
                                    System.out.println("文件名:" + fileName + ";方法名:" + methodName + ";数据库表名:" + simpleTableList.get(h) + ";操作:select");
                                    insertTableBeanSet.add(new InsertTableBean(fileName, methodName, simpleTableList.get(h), SELECT));
                                }
                            } else {
                                System.out.println("文件名:" + fileName + ";方法名:" + methodName + ";数据库表名:" + sqlTrimString + ";操作:select");
                                insertTableBeanSet.add(new InsertTableBean(fileName, methodName, sqlTrimString, SELECT));
                            }
                        } else {
                            if (sqlTrimString.contains(",")) {
                                List<String> simpleTableList = Arrays.asList(sqlTrimString.split(","));
                                for (int h = 0; h < simpleTableList.size(); h++) {
                                    System.out.println("文件名:" + fileName + ";方法名:" + methodName + ";数据库表名:" + simpleTableList.get(h) + ";操作:select");
                                    insertTableBeanSet.add(new InsertTableBean(fileName, methodName, simpleTableList.get(h), SELECT));
                                }
                            } else {
                                System.out.println("文件名:" + fileName + ";方法名:" + methodName + ";数据库表名:" + sqlTrimString + ";操作:select");
                                insertTableBeanSet.add(new InsertTableBean(fileName, methodName, sqlTrimString, SELECT));
                            }
                        }
                    }
                }
            }

        }
    }
}

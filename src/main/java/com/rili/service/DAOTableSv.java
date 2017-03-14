package com.rili.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

/**
 * Author： fanyafeng
 * Data： 17/3/14 10:23
 * Email: fanyafeng@live.cn
 */
@Service
public class DAOTableSv {
    private static final Logger LOGGER = LoggerFactory.getLogger(DAOTableSv.class);

    public void test(String fullFilePath) {
        if (fullFilePath.contains("/")) {
            int fileNameLength = fullFilePath.split("/").length;
            String fileName = Arrays.asList(fullFilePath.split("/")).get(fileNameLength - 1);
            try {
                FileInputStream fileInputStream = new FileInputStream(fullFilePath);
                CompilationUnit compilationUnit = JavaParser.parse(fileInputStream);
                new MethodChangerVisitor(fileName).visit(compilationUnit, null);
            } catch (FileNotFoundException e) {
                LOGGER.error("FileNotFoundException:{}", e);
            }
        }
    }

    private static class MethodChangerVisitor extends VoidVisitorAdapter<Void> {

        private String fileName;

        public MethodChangerVisitor(String fileName) {
            this.fileName = fileName;
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
                    } else if (simpleTable.contains("(")) {
                        String currentTable = simpleTable.substring(0, simpleTable.indexOf("("));
                        System.out.println("文件名:" + fileName + ";方法名:" + methodName + ";数据库表名:" + currentTable + ";操作:insert");
                    }
                } else if (node.contains("update ")) {//改
                    String currentTable = node.substring(node.indexOf("update ") + 6, node.indexOf("set") - 1).trim();
                    System.out.println("文件名:" + fileName + ";方法名:" + methodName + ";数据库表名:" + currentTable + ";操作:update");
                } else if (node.contains("delete from ")) {//删
                    String currentTable = node.substring(node.indexOf("delete from ") + 12, node.indexOf("where"));
                    System.out.println("文件名:" + fileName + ";方法名:" + methodName + ";数据库表名:" + currentTable + ";操作:delete");
                } else if (node.contains("select") && node.contains("where")) {//有where的查
                    List<String> simpleTableList = Arrays.asList(node.split("from "));
                    for (int j = 0; j < simpleTableList.size(); j++) {
                        if (j > 0) {
                            String simpleTable = simpleTableList.get(j).trim();
                            if (!simpleTable.contains("select")) {
                                if (j == simpleTableList.size() - 1 && simpleTable.contains(")") && !simpleTable.contains(" ")) {
                                    System.out.println("文件名:" + fileName + ";方法名:" + methodName + ";数据库表名:" + simpleTable.substring(0, simpleTable.indexOf(")")) + ";操作:select");
                                } else {
                                    System.out.println("文件名:" + fileName + ";方法名:" + methodName + ";数据库表名:" + simpleTable.substring(0, simpleTable.indexOf(" ")) + ";操作:select");
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
                                            }
                                        } else {
                                            System.out.println("文件名:" + fileName + ";方法名:" + methodName + ";数据库表名:" + selectGroupTable + ";操作:select");
                                        }
                                    } else {
                                        String selectGroupTable = simpleJoinTable.substring(0, simpleJoinTable.indexOf(" "));
                                        if (selectGroupTable.contains(",")) {
                                            List<String> groupList = Arrays.asList(selectGroupTable.split(","));
                                            for (int h = 0; h < groupList.size(); h++) {
                                                String selectItemTable = groupList.get(h);
                                                System.out.println("文件名:" + fileName + ";方法名:" + methodName + ";数据库表名:" + selectItemTable + ";操作:select");
                                            }
                                        } else {
                                            System.out.println("文件名:"+fileName+";方法名:"+methodName+";数据库表名:"+selectGroupTable+";操作:select");
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
                                    System.out.println("文件名:"+fileName+";方法名:"+methodName+";数据库表名:"+currentTableList.get(h)+";操作:select");
                                }
                            } else {
                                System.out.println("文件名:"+fileName+";方法名:"+methodName+";数据库表名:"+currentTable+";操作:select");
                            }
                        } else {
                            if (simpleTable.contains(",")) {
                                List<String> simpleTableList = Arrays.asList(simpleTable.split(","));
                                for (int h = 0; h < simpleTableList.size(); h++) {
                                    System.out.println("文件名:"+fileName+";方法名:"+methodName+";数据库表名:"+simpleTableList.get(h)+";操作:select");
                                }
                            } else {
                                System.out.println("文件名:"+fileName+";方法名:"+methodName+";数据库表名:"+simpleTable+";操作:select");
                            }
                        }
                    } else {
                        String sqlTrimString = sqlString.trim();
                        if (sqlTrimString.contains(" ")) {
                            String currentString = sqlTrimString.substring(0, sqlTrimString.indexOf(" "));
                            if (currentString.contains(",")) {
                                List<String> simpleTableList = Arrays.asList(currentString.split(","));
                                for (int h = 0; h < simpleTableList.size(); h++) {
                                    System.out.println("文件名:"+fileName+";方法名:"+methodName+";数据库表名:"+simpleTableList.get(h)+";操作:select");
                                }
                            } else {
                                System.out.println("文件名:"+fileName+";方法名:"+methodName+";数据库表名:"+sqlTrimString+";操作:select");
                            }
                        } else {
                            if (sqlTrimString.contains(",")) {
                                List<String> simpleTableList = Arrays.asList(sqlTrimString.split(","));
                                for (int h = 0; h < simpleTableList.size(); h++) {
                                    System.out.println("文件名:"+fileName+";方法名:"+methodName+";数据库表名:"+simpleTableList.get(h)+";操作:select");
                                }
                            } else {
                                System.out.println("文件名:"+fileName+";方法名:"+methodName+";数据库表名:"+sqlTrimString+";操作:select");
                            }
                        }
                    }
                }
            }

        }
    }
}

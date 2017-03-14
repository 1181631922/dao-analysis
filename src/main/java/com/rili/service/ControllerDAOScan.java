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
public class ControllerDAOScan {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerDAOScan.class);

    public void test(String fullFilePath) {
        if (fullFilePath.contains("/")) {
            int fileNameLength = fullFilePath.split("/").length;
            String fileName = Arrays.asList(fullFilePath.split("/")).get(fileNameLength - 1);
            try {
                FileInputStream fileInputStream = new FileInputStream(fullFilePath);
                CompilationUnit compilationUnit = JavaParser.parse(fileInputStream);
                new MethodChangerVisitor().visit(compilationUnit, null);
            } catch (FileNotFoundException e) {
                LOGGER.error("FileNotFoundException:{}", e);
            }
        }
    }

    private static class MethodChangerVisitor extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(MethodDeclaration n, Void arg) {
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
//                        System.out.println("  |---insert->" + currentTable);
                    } else if (simpleTable.contains("(")) {
                        String currentTable = simpleTable.substring(0, simpleTable.indexOf("("));
//                        System.out.println("  |---insert->" + currentTable);
                    }
                } else if (node.contains("update ")) {//改
//                    System.out.println(node);
                    String currentTable = node.substring(node.indexOf("update ") + 6, node.indexOf("set") - 1).trim();
//                    System.out.println("  |---update->" + currentTable);
                } else if (node.contains("delete from ")) {//删
//                    System.out.println(node);
                    String currentTable = node.substring(node.indexOf("delete from ") + 12, node.indexOf("where"));
//                    System.out.println("  |---delete->" + currentTable);
                } else if (node.contains("select") && node.contains("where")) {//有where的查
//                    System.out.println(node);
                    List<String> simpleTableList = Arrays.asList(node.split("from "));
                    for (int j = 0; j < simpleTableList.size(); j++) {
//                        System.out.println(simpleTableList.get(j));
                        if (j > 0) {
                            String simpleTable = simpleTableList.get(j).trim();
                            if (!simpleTable.contains("select")) {
                                if (j == simpleTableList.size() - 1 && simpleTable.contains(")") && !simpleTable.contains(" ")) {
                                    System.out.println(simpleTable.substring(0, simpleTable.indexOf(")")));
                                } else {
                                    System.out.println(simpleTable.substring(0, simpleTable.indexOf(" ")));
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
                                                System.out.println("2222222222" + selectItemTable);
                                            }
                                        } else {
                                            System.out.println("333333333" + selectGroupTable);
                                        }
                                    } else {
                                        String selectGroupTable = simpleJoinTable.substring(0, simpleJoinTable.indexOf(" "));
                                        if (selectGroupTable.contains(",")) {
                                            List<String> groupList = Arrays.asList(selectGroupTable.split(","));
                                            for (int h = 0; h < groupList.size(); h++) {
                                                String selectItemTable = groupList.get(h);
                                                System.out.println("44444444" + selectItemTable);
                                            }
                                        } else {
                                            System.out.println("55555555555" + selectGroupTable);
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
                            System.out.println("  |---select->" + currentTable);
                            if (currentTable.contains(",")) {
                                List<String> currentTableList = Arrays.asList(currentTable.split(","));
                                for (int h = 0; h < currentTableList.size(); h++) {
                                    System.out.println("11111111111" + currentTableList.get(h));
                                }
                            } else {
                                System.out.println("2222222222" + currentTable);
                            }
                        } else {
                            System.out.println("  |---select->" + simpleTable);
                            if (simpleTable.contains(",")) {
                                List<String> simpleTableList = Arrays.asList(simpleTable.split(","));
                                for (int h = 0; h < simpleTableList.size(); h++) {
                                    System.out.println("33333333" + simpleTableList.get(h));
                                }
                            } else {
                                System.out.println("44444444444" + simpleTable);
                            }
                        }
                    } else {
                        String sqlTrimString = sqlString.trim();
                        if (sqlTrimString.contains(" ")) {
                            String currentString = sqlTrimString.substring(0, sqlTrimString.indexOf(" "));
                            if (currentString.contains(",")) {
                                List<String> simpleTableList = Arrays.asList(currentString.split(","));
                                for (int h = 0; h < simpleTableList.size(); h++) {
                                    System.out.println("555" + simpleTableList.get(h));
                                }
                            } else {
                                System.out.println("66666" + sqlTrimString);
                            }
                        } else {
                            if (sqlTrimString.contains(",")) {
                                List<String> simpleTableList = Arrays.asList(sqlTrimString.split(","));
                                for (int h = 0; h < simpleTableList.size(); h++) {
                                    System.out.println("77777" + simpleTableList.get(h));
                                }
                            } else {
                                System.out.println("88888" + sqlTrimString);
                            }
                        }
                    }
                }
            }

        }
    }
}

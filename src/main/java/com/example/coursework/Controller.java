package com.example.coursework;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class Controller {
    @FXML
    private TextField folderPathTextField;
    Map<Long, ArrayList<Pair<String, Boolean>>> map;
    private ArrayList<ArrayList<String>> duplicateFiles;

    //поиск всех файлов директории
    private void searchFiles(File rootFile, List<File> fileList) {
        if (rootFile.isDirectory()) {
            File[] directoryFiles = rootFile.listFiles();
            if (directoryFiles != null) {
                for (File file: directoryFiles) {
                    if (file.isDirectory()) {
                        searchFiles(file, fileList);
                    } else {
                        fileList.add(file);
                    }
                }
            }
        }
    }

    private void distributionFilesInMap(File directory) {
        map = new HashMap<>();
        if (directory != null) {
            // путь к выбранной папке
            String path = directory.getAbsolutePath();
            folderPathTextField.setText(path);
            // массив со всеми файлами в папке
            ArrayList<File> fileList = new ArrayList<>();
            searchFiles(directory, fileList);
            for (File file : fileList) {
                // проверяем есть ли уже файлы с таким объемом, если нет, то создаем новую пару в мэпе,
                // если есть, то добавляем путь к файлу в массив
                if (!map.containsKey(file.length())) {
                    map.put(file.length(), new ArrayList<>());
                    map.get(file.length()).add(new Pair<>(file.getAbsolutePath(), false));
                } else {
                    map.get(file.length()).add(new Pair<>(file.getAbsolutePath(), false));
                }
            }
        }
    }

    private boolean compareByteArrays(byte[] arr1, byte[] arr2) {
        if (arr1.length != arr2.length) {
            return false;
        }

        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i] != arr2[i]) {
                return false;
            }
        }
        return true;
    }

    private boolean compareFiles(String file1, String file2) throws IOException {
        int CHUNK_SIZE = 1024;
        // получает входные байты из файла в файловой системе
        FileInputStream fis1 = new FileInputStream(file1);
        FileInputStream fis2 = new FileInputStream(file2);
        BufferedInputStream bis1 = new BufferedInputStream(fis1, CHUNK_SIZE);
        BufferedInputStream bis2 = new BufferedInputStream(fis2, CHUNK_SIZE);

        byte[] buffer1 = new byte[CHUNK_SIZE];
        byte[] buffer2 = new byte[CHUNK_SIZE];

        int byteR1 = bis1.read(buffer1);
        int byteR2 = bis2.read(buffer2);

        while (byteR1 != -1 || byteR2 != -1) {
            if (!compareByteArrays(buffer1, buffer2)) {
                fis1.close();
                fis2.close();
                bis1.close();
                bis2.close();
                return false;
            }
            byteR1 = bis1.read(buffer1);
            byteR2 = bis2.read(buffer2);
        }
        fis1.close();
        fis2.close();
        bis1.close();
        bis2.close();
        return true;
    }

    private void compareFilesInMap() throws IOException {
        for (Long key : map.keySet()) {
            // массив с одинаковыми файлами под одним ключом
            ArrayList<String> identicalFilesBySize = new ArrayList<>();
            // массив пар с одним размером
            ArrayList<Pair<String, Boolean>> elementsInOneKey = map.get(key);
            for (int i = 0; i < elementsInOneKey.size() - 1; i++) {
                for (int j = i + 1; j < elementsInOneKey.size(); j++) {
                    if (compareFiles(elementsInOneKey.get(i).getKey(), elementsInOneKey.get(j).getKey())) {
                        // если еще не добавлен елемент, то добавляем в массив одинаковых файлов и ставим значение тру
                        if (!elementsInOneKey.get(i).getValue()) {
                            identicalFilesBySize.add(elementsInOneKey.get(i).getKey());
                            elementsInOneKey.set(i, new Pair<>(elementsInOneKey.get(i).getKey(), true));
                        }

                        if (!elementsInOneKey.get(j).getValue()) {
                            identicalFilesBySize.add(elementsInOneKey.get(j).getKey());
                            elementsInOneKey.set(j, new Pair<>(elementsInOneKey.get(j).getKey(), true));
                        }
                    }
                }
            }
            if (identicalFilesBySize.size() != 0) {
                duplicateFiles.add(identicalFilesBySize);
            }
        }
    }

    private void setTable() {
        TableView<Data> table = new TableView<>();

        TableColumn<Data, Integer> groupCol = new TableColumn<>("Группа");
        groupCol.setCellValueFactory(new PropertyValueFactory<>("groupNumber"));

        TableColumn<Data, String> nameCol = new TableColumn<>("Название файла");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Data, String> pathCol = new TableColumn<>("Путь к файлу");
        pathCol.setCellValueFactory(new PropertyValueFactory<>("path"));

        table.getColumns().setAll(groupCol, nameCol, pathCol);

        ObservableList<Data> list = FXCollections.observableArrayList();

        int groupNumber = 1;
        for (ArrayList<String> pathList : duplicateFiles) {
            for (String path : pathList) {
                list.add(new Data(groupNumber, Paths.get(path).getFileName().toString(), path));
            }
            groupNumber++;
        }

        table.setItems(list);

        // вывод таблицы на экран
        Stage stage = new Stage();
        Scene scene = new Scene(new StackPane(table), 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void chooseDirectory(ActionEvent event) throws IOException {
        // создание экземпляра класса DirectoryChooser
        DirectoryChooser directoryChooser = new DirectoryChooser();
        // отображение диалогового окна и получение выбранной папки
        File selectedDirectory = directoryChooser.showDialog(null);
        // создание словаря (размер файла, список[путь к файлу, путь к файлу, ...])

        // выполнение действий с выбранной папкой
        distributionFilesInMap(selectedDirectory);

        // создаем двумерный массив с одинаковыми файлами
        duplicateFiles = new ArrayList<>();
        // сравнение файлов
        compareFilesInMap();

        setTable();
    }

    @FXML
    protected void removeDuplicates(ActionEvent event) {
        if (duplicateFiles.size() == 0) {
            return;
        }
        for (ArrayList<String> duplicateFile : duplicateFiles) {
            for (int j = 1; j < duplicateFile.size(); j++) {
                File file = new File(duplicateFile.get(j));
                if (file.delete()) {
                    System.out.println("Файл удален");
                }
            }
        }
        duplicateFiles.subList(1, duplicateFiles.size()).clear();
    }
}


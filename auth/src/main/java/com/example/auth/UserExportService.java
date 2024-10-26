package com.example.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class UserExportService {

    @Autowired
    private UserDataRepository userDataRepository;

    public byte[] exportUsersToCsv() throws IOException {
        List<UserData> users = userDataRepository.findAll();
        StringBuilder csvContent = new StringBuilder();
        csvContent.append("First Name,Last Name,Email,Position\n");

        for (UserData user : users) {
            csvContent.append(String.format("%s,%s,%s,%s\n",
                    escapeSpecialCharacters(user.getFirstName()),
                    escapeSpecialCharacters(user.getLastName()),
                    escapeSpecialCharacters(user.getEmail()),
                    escapeSpecialCharacters(user.getPosition())));
        }

        return csvContent.toString().getBytes();
    }

    private String escapeSpecialCharacters(String data) {
        if (data == null) {
            return "";
        }
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}
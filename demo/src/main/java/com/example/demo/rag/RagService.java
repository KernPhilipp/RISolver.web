package com.example.demo.rag;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
public class RagService {
    @Value("${rag.script.path}")
    private String scriptPath;

    public String getAnswer(String question) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "py", scriptPath, question
            );
            pb.redirectErrorStream(false);
            Process process = pb.start();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            process.waitFor();
            return output.toString().trim();
        } catch (Exception e) {
            throw new RuntimeException("Error executing RAG script", e);
        }
    }
}

package com.example.Indrugs.services;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

@Service
public class ImgBBService {

    @Value("${imgbb.api.key}")
    private String apiKey;

    private final String API_URL = "https://api.imgbb.com/1/upload";

    public String uploadImage(MultipartFile file) throws Exception {

        // Convertir imagen a Base64
        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("key", apiKey)
                .add("image", base64Image)
                .build();

        Request request = new Request.Builder()
                .url("https://api.imgbb.com/1/upload")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new RuntimeException("Error subiendo imagen a ImgBB: " + response);
        }

        String json = response.body().string();

        // Extraer URL de la respuesta JSON
        String url = json.split("\"url\":\"")[1].split("\"")[0];

        return url;
    }

}


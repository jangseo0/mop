package com.example.finalproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {
    private EditText edtTitle, edtAuthor, edtPages, edtReadPages;
    private RatingBar edtRating;
    private ImageView bookImage;
    private Uri selectedImageUri;
    private Button submitButton;
    private static final String BOOK_DATA_FILE = "book_data.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        setTitle("도서 추가");

        edtTitle = findViewById(R.id.bookTitleEditText);
        edtAuthor = findViewById(R.id.bookAuthorEditText);
        edtPages = findViewById(R.id.totalPagesEditText);
        edtReadPages = findViewById(R.id.pagesReadEditText);
        edtRating = findViewById(R.id.ratingBar);
        submitButton = findViewById(R.id.submitButton);
        bookImage = findViewById(R.id.bookImage);

        // JSON 파일 초기화
        ensureJsonFileExists();

        // 이미지 선택 버튼 동작
        bookImage.setOnClickListener(v -> openImagePicker());

        // 'submitButton' 클릭 이벤트 (데이터 저장 및 반환)
        submitButton.setOnClickListener(v -> saveBookDataAndReturn());
    }

    // JSON 파일이 없으면 빈 JSON 파일 생성
    private void ensureJsonFileExists() {
        File file = new File(getFilesDir(), BOOK_DATA_FILE);
        if (!file.exists()) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write("[]".getBytes()); // 빈 JSON 배열로 초기화
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 갤러리에서 이미지 선택
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            bookImage.setImageURI(selectedImageUri);
        }
    }

    // 데이터 저장 및 MainActivity로 돌아가기
    private void saveBookDataAndReturn() {
        try {
            // 입력된 데이터 가져오기
            String title = edtTitle.getText().toString().trim();
            String author = edtAuthor.getText().toString().trim();
            String totalPagesStr = edtPages.getText().toString().trim();
            String readPagesStr = edtReadPages.getText().toString().trim();

            if (title.isEmpty() || author.isEmpty() || totalPagesStr.isEmpty() || readPagesStr.isEmpty()) {
                Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            int totalPages = Integer.parseInt(totalPagesStr);
            int readPages = Integer.parseInt(readPagesStr);

            if (readPages > totalPages) {
                Toast.makeText(this, "읽은 분량은 총 페이지 수를 초과할 수 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            float rating = edtRating.getRating();
            double progress = ((double) readPages / totalPages) * 100;

            // 선택된 이미지 URI 확인
            if (selectedImageUri == null) {
                Toast.makeText(this, "이미지를 등록해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Book 객체 생성
            Book book = new Book(title, author, totalPages, readPages, rating, progress, selectedImageUri.toString());

            // 인텐트를 통해 Book 객체 반환
            Intent resultIntent = new Intent();
            resultIntent.putExtra("bookData", book);
            setResult(RESULT_OK, resultIntent);
            finish();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "총 페이지 수와 읽은 분량은 숫자로 입력해야 합니다.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "도서 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}




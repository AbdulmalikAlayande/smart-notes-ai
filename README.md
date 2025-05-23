# 🧠 SmartNotesAI

SmartNotesAI is a fast and intelligent web application that helps users take notes, organize them into folders, and utilize powerful AI features like automatic **summarization** and **tag generation**. It supports multiple LLM providers such as **OpenAI**, **Anthropic**, **Ollama**, **Grok**, and **DeepSeek** for enhanced flexibility and resilience.

---

## 🚀 Features

- 🔐 **User Authentication**: Secure registration and login system.
- 📁 **Folder Management**: Create, update, and delete folders for organizing notes.
- 📝 **Note Management**: Full CRUD operations for notes.
- 🧠 **AI-Powered Summarization**: Generate concise summaries and key points from note content.
- 🏷️ **AI Tag Generation**: Suggest relevant tags for notes using LLMs.
- 🌐 **Multi-Provider LLM Support**: Easily switch between supported AI providers.
- ✅ **Provider Health Tracking**: Selects and monitors healthy LLMs dynamically.
- ⚙️ **Asynchronous Processing**: Fast, non-blocking AI requests.
- 🔗 **RESTful API**: Clean and consistent API structure.

---

## ⚙️ How It Works

### 1. User Flow

1. Register or log in.
2. Create folders to categorize notes.
3. Add or update notes.
4. When saving, the backend:
   - Generates a summary and key points.
   - Suggests relevant tags using AI.
5. View, edit, or delete notes and folders as needed.

### 2. AI Integration

- Upon note creation or update, the note content is sent to the selected LLM provider.
- The LLM returns a **summary**, **key points**, and **tags**.
- These results are saved and served back to the user.

### 3. LLM Provider Selection

- The system supports multiple LLMs and intelligently selects a healthy one based on availability and performance.

---

## 🛠️ Tech Stack

| Layer       | Technology |
|-------------|------------|
| **Backend** | Java, Spring Boot, Maven |
| **AI**      | Spring AI, OpenAI, Anthropic, Ollama, Grok, DeepSeek |
| **Database**| SQL (PostgreSQL, MySQL, etc.) |
| **API**     | RESTful JSON endpoints |
| **Build**   | Maven |

---

## 🧰 Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker (for Ollama or local LLMs, optional)
- API keys for:
  - OpenAI
  - Anthropic
  - Grok
  - DeepSeek

### 🔧 Setup Instructions

```bash
# 1. Clone the repository
git clone https://github.com/yourusername/smartnotesai.git
cd smartnotesai

# 2. Configure environment
cp src/main/resources/application.example.yml src/main/resources/application.yml
# Edit the file with your DB and API credentials

# 3. Build the application
mvn clean install

# 4. Run the application
mvn spring-boot:run
# The server will start at http://localhost:8080
```

## 📖 API Documentation

The backend exposes a RESTful API for user authentication, folder and note management, and AI-powered endpoints.

<details>
   <summary>
      <strong>🔐 Authentication</strong>
   </summary>
   
   ### Register  
   **POST** `/api/auth/register`
   
   ```json
   {
     "username": "string",
     "password": "string"
   }
   ```
   Response:
   
   ```json
   {
      
   }
   ```

   ### Login
   **POST** `/api/auth/login`
   
   ```json
   {
     "username": "string",
     "password": "string"
   }
   ```
   Response:
   
   ```json
   {
     "token": "string"
   }
   ```
</details> 

<details> 
   <summary>
      <strong>📁 Folder Management</strong>
   </summary>
   
   ### Create Folder
   **POST** `/api/folders`
   
   ```json
   {
     "name": "string"
   }
   ```
   Response:
   
   ```json
   {
      
   }
   ```

   ## Get All Folders
   **GET** `/api/folders`
   
   Response:
   
   ```json
   [
     {
       "id": 1,
       "name": "Work Notes"
     }
   ]
   ```

   ### Update Folder
   **PUT** `/api/folders/{public-id}`

   Response: 
   ```json
   {
      
   }
   ```

   ### Delete Folder
   **DELETE** `/api/folders/{id}`

   Response: 
   ```json
   {
      
   }
   ```
</details> 
<details> 
   <summary>
      <strong>📝 Note Management</strong>
   </summary>
   
   ### Create Note
   POST /api/notes
   
   json
   Copy
   Edit
   {
     "title": "string",
     "content": "string",
     "folderId": 1
   }
   Get All Notes
   GET /api/notes
   
   Update Note
   PUT /api/notes/{id}
   
   Delete Note
   DELETE /api/notes/{id}

</details>

<details> <summary><strong>🧠 AI Features</strong></summary>
Summarize Note
POST /api/ai/summarize

json
Copy
Edit
{
  "content": "Your full note content here"
}
Response:

json
Copy
Edit
{
  "summary": "A brief summary...",
  "keyPoints": ["Point 1", "Point 2"]
}
Generate Tags
POST /api/ai/tags

json
Copy
Edit
{
  "content": "Note content for tagging"
}
Response:

json
Copy
Edit
{
  "tags": ["Tag1", "Tag2", "Tag3"]
}
</details> ```

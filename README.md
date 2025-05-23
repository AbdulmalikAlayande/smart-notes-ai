# SmartNotesAI

SmartNotesAI is a fast, practical web application that enables users to take notes, organize them into folders, and leverage AI-powered features such as automatic tagging and summarization. The backend is built with Java, Spring Boot, and integrates with multiple LLM providers (OpenAI, Anthropic, Ollama, Grok, DeepSeek) for advanced note intelligence.

---

## Features

- **User Authentication:** Secure registration and login for users.
- **Folder Management:** Create, update, and delete folders to organize notes.
- **Note Management:** Create, edit, delete, and view notes within folders.
- **AI-Powered Summarization:** Generate concise summaries and key points for notes using LLMs.
- **AI Tag Generation:** Automatically suggest relevant tags for notes.
- **Multi-Provider LLM Support:** Switch between OpenAI, Anthropic, Ollama, Grok, and DeepSeek for AI features.
- **Provider Health Tracking:** Monitors and selects healthy AI providers for resilience.
- **Asynchronous Processing:** Fast, non-blocking AI operations for a smooth user experience.
- **RESTful API:** Clean, well-structured endpoints for all operations.

---

## How It Works

1. **User Flow:**
   - Register or log in.
   - Create folders to organize notes.
   - Add notes to folders.
   - On note creation or update, the system can:
     - Summarize the note content.
     - Suggest tags using AI.
   - Users can view, edit, or delete notes and folders.

2. **AI Integration:**
   - When a note is created/updated, the backend sends the content to the selected LLM provider.
   - The provider returns a summary, key points, and tags.
   - Results are stored and displayed to the user.

3. **Provider Selection:**
   - The system can dynamically select from available LLM providers based on health and configuration.

---

## Tech Stack

- **Backend:** Java, Spring Boot, Maven
- **AI Integration:** Spring AI, OpenAI, Anthropic, Ollama, Grok, DeepSeek
- **Database:** SQL (configurable)
- **API:** RESTful endpoints
- **Build Tool:** Maven

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- (Optional) Docker (for Ollama or local LLMs)
- API keys for OpenAI, Anthropic, Grok and DeepSeek.

### Setup

1. **Clone the repository:**
   ```sh
   git clone https://github.com/yourusername/smartnotesai.git
   cd smartnotesai


# API Documentation: The SmartNotesAI application provides a RESTful API for managing users, folders, notes, and AI-powered features like summarization and tag generation.

## Authentication

### Register
**POST** `/api/auth/register`  
Registers a new user.

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}

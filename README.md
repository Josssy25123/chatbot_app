#  Morgan State University AI Chatbot Mobile App

<div align="center">

![Morgan State University](https://img.shields.io/badge/Morgan%20State-University-orange?style=for-the-badge)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)

**An intelligent AI-powered mobile assistant designed specifically for Morgan State University students**

</div>

---

##  About

The **Morgan State AI Chatbot App** is an innovative mobile application that provides personalized academic assistance to students at Morgan State University. Powered by OpenAI's GPT technology and integrated with university-specific data, this chatbot helps students navigate their academic journey with ease.

###  Purpose

This app was developed to:
- **Enhance student success** through instant AI-powered academic support
- **Provide 24/7 assistance** for course information, curriculum guidance, and general queries
- **Streamline information access** with Morgan State University's curriculum data
- **Support students** with accessible, user-friendly technology

---

##  Features

###  AI-Powered Chat
- **Intelligent Responses**: Leverages OpenAI GPT-4 for natural, context-aware conversations
- **University-Specific Knowledge**: Trained on Morgan State University curriculum data
- **Multi-turn Conversations**: Maintains context throughout the chat session
- **Real-time Typing Indicators**: Visual feedback during AI response generation

###  Curriculum Information
- **Course Catalog**: Browse Morgan State's complete course offerings
- **Department Information**: Access faculty details and contact info
- **Smart Search**: Find courses and professors quickly
- **Firebase Integration**: Real-time curriculum data updates

###  User Profiles
- **Firebase Authentication**: Secure sign-in with email/password
- **Anonymous Mode**: Use without creating an account
- **Profile Management**: View and edit user information
- **Secure Logout**: Easy session management

###  Modern UI/UX
- **Material Design 3**: Beautiful, modern interface
- **Dark/Light Modes**: Automatic theme switching
- **ChatGPT-inspired Design**: Familiar, professional layout
- **Responsive Design**: Optimized for all screen sizes

---

##  Installation

### Prerequisites

- **Android Studio**: Hedgehog (2023.1.1) or newer
- **Minimum SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 14 (API 35)
- **Kotlin**: 1.9.0+
- **Firebase Account**: For backend services
- **OpenAI API Key**: For chatbot functionality

### Setup Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/Sakinastha/chatbot-mobile-app-.git
   cd chatbot-mobile-app-
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory

3. **Configure Firebase**
   - Create a new project in [Firebase Console](https://console.firebase.google.com/)
   - Add an Android app with package name: `com.example.chatbotapp`
   - Download `google-services.json`
   - Place it in the `app/` directory
   - Enable Authentication (Email/Password & Anonymous)
   - Enable Firestore Database
   - Enable Storage

4. **Add OpenAI API Key**
   
   Create `local.properties` in root directory:
   ```properties
   OPENAI_API_KEY=your_openai_api_key_here
   ```

   Or update `OpenAIService.kt`:
   ```kotlin
   private val apiKey = "your_openai_api_key_here"
   ```

5. **Sync and Build**
   ```bash
   ./gradlew build
   ```

6. **Run the App**
   - Connect your Android device or start an emulator
   - Click "Run" in Android Studio

---

##  Tech Stack

### Frontend
- **Kotlin**: Primary programming language
- **Jetpack Compose**: Modern declarative UI toolkit
- **Material Design 3**: UI components and theming
- **Navigation Compose**: Screen navigation
- **Coroutines**: Asynchronous programming

### Backend & Services
- **Firebase Authentication**: User management
- **Cloud Firestore**: Real-time database
- **Firebase Storage**: File storage
- **OpenAI GPT-4**: AI conversational engine
- **Retrofit**: HTTP client for API calls

### Architecture & Libraries
- **MVVM Pattern**: Clean architecture
- **StateFlow**: Reactive state management
- **Dependency Injection**: Manual DI
- **Android Speech Recognizer**: Voice input
- **Gson**: JSON serialization

---

##  Architecture

### MVVM Pattern

```
View (Jetpack Compose)
    ↓
ViewModel (State Management)
    ↓
Repository (Data Layer)
    ↓
Data Sources (Firebase, OpenAI API)
```

### Data Flow

1. **User Input** → Chat Screen
2. **State Update** → ViewModel processes input
3. **API Call** → OpenAI Service sends request
4. **Response** → Parsed and formatted
5. **Firebase Save** → Chat history persisted
6. **UI Update** → New message displayed

---

##  Contributing

We welcome contributions from the Morgan State community!

### How to Contribute

1. **Fork the Repository**
2. **Create a Feature Branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Commit Your Changes**
   ```bash
   git commit -m "Add amazing feature"
   ```
4. **Push to Branch**
   ```bash
   git push origin feature/amazing-feature
   ```
5. **Open a Pull Request**

### Contribution Guidelines

- Follow Kotlin coding conventions
- Write clear commit messages
- Add tests for new features
- Update documentation
- Ensure all tests pass

---

##  License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---


</div>

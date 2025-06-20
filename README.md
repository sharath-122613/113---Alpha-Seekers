# Legal Finance Buddy

A Java-based AI assistant for legal and financial document processing.

## Features
- Contract generation with PDF export
- Legal notice analysis
- Filing document preparation
- Integration with OpenAI's GPT-4 for intelligent document processing
- PDF document generation and validation

## Prerequisites
- Java 17 or higher
- Maven
- OpenAI API key

## Setup
1. Clone the repository
2. Update `src/main/resources/application.properties` with your OpenAI API key
3. Run `mvn clean install` to build the project
4. Start the application with `mvn spring-boot:run`

## API Endpoints

### Generate Contract
- **Endpoint**: POST `/api/legal-buddy/generate-contract`
- **Request Body**: `LegalDocumentRequest`
  ```json
  {
    "template": "Contract template text",
    "businessDetails": "Business details",
    "generatePdf": true/false
  }
  ```
- **Response**: Either text content or PDF file (based on generatePdf flag)

### Analyze Notice
- **Endpoint**: POST `/api/legal-buddy/analyze-notice`
- **Request Body**: `LegalDocumentRequest`
  ```json
  {
    "notice": "Legal notice text"
  }
  ```
- **Response**: Analysis of the legal notice

### Prepare Filing
- **Endpoint**: POST `/api/legal-buddy/prepare-filing`
- **Request Body**: `LegalDocumentRequest`
  ```json
  {
    "filingType": "Type of filing",
    "businessDetails": "Business details",
    "generatePdf": true/false
  }
  ```
- **Response**: Either text content or PDF file (based on generatePdf flag)

## Usage
The application uses OpenAI's GPT-4 to process and generate legal and financial documents. Make sure to provide appropriate templates and business details for best results.

## Error Handling
The API returns appropriate HTTP status codes:
- 200 OK - Success
- 400 Bad Request - Invalid input
- 500 Internal Server Error - Processing error

## Logging
The application logs important information and errors for debugging purposes. Check logs for detailed error messages if something goes wrong.

#   Web_RestAssured_rem_waste_assignment
# RemWaste Automation Project

End-to-end automation framework for testing RemWaste's Web UI and Backend APIs — including user login, product add/delete, order placement, and verification.

---

## Tech Stack

| Area       | Tools Used                          |
|------------|-------------------------------------|
| API Tests  | Java + RestAssured + TestNG         |
| UI Tests   | Java + Selenium WebDriver + TestNG  |
| Reporting  | TestNG Reports                      |
| Build Tool | Maven                               |
| Design     | Page Object Model (POM)             |

---

##  Project Structure

├── pom.xml
├── testng.xml
├── README.md
├── /src
│ ├── /main
│ │ └── /java/org/rem_waste/
│ │ ├── pageObjects/ # UI Pages
│ │ ├── pojo/ # Request/Response Classes
│ │ └── utils/ # ConfigReader, Enums, etc.
│ └── /test
│ └── /java/org/rem_waste/
│ ├── apiTests/ # API test classes
│ └── uiTests/ # UI test classes


##  Deliverables

- ✅ API test automation for login, product, cart, and order flows
- ✅ UI test automation for login and product visibility
- ✅ Token-based authorization with API chaining
- ✅ JSON schema validation for response bodies
- ✅ Page Object Model for UI tests
- ✅ Configurable via `.properties` file
- ✅ Organized TestNG suite (`testng.xml`)
- ✅ Simple and fast setup (1–2 mins)

---

## Setup Instructions (1–2 mins)

### Prerequisites

- Java 17+
- Maven 3.6+
- Chrome browser installed
- IDE (e.g., IntelliJ, Eclipse)

### Clone the Repository

```bash
git clone https://github.com/your-username/remwaste-automation.git
cd remwaste-automation

Sample Scenarios Covered
Login via API (token-based)

Add product (with image)

Verify product on UI

Add product to cart (optional)

Place order

Fetch order details

Delete product

Assert expected vs actual responses

## Assumptions & Notes
Image upload path is hardcoded — update for your local path

Base URL and user credentials are read from config.properties

Token is stored and reused during the test session

Local Chrome is used for UI testing — no grid execution

Basic validations — not performance or security testing

## Enhancements (Optional for Future)
1. CI pipeline (GitHub Actions or Jenkins)

2.  Allure Reports or ExtentReports

3.  Docker support for execution

4.  Parallel test execution

5.Data-driven testing via Excel or JSON

6. Visual regression using Percy or Applitools

🤝 Author
Amol Aldar
🔗 LinkedIn: https://www.linkedin.com/in/amol-aldar-994505267/
💻 Passionate about automation, API, and full-stack QA
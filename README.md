# MyCelia 🍄

[![CI Status](https://github.com/se-edu/addressbook-level3/workflows/Java%20CI/badge.svg)](https://github.com/AY2526S2-CS2103T-W11-4/tp)

> \_Keeping your business network connected.\_

MyCelia is a command-line-driven desktop application for B2B delivery coordinators. Manage your business contacts and track outgoing deliveries — all from a single keyboard-driven interface. No clicking around. Just type and go.

![MyCelia Main Window](docs/images/main-window.png)

\---

## What's Inside

MyCelia runs two books simultaneously:

* **Company Book** — your directory of business partners, suppliers, and clients
* **Delivery Book** — your log of outgoing deliveries linked to those companies

Switch between them with a single command or via the UI tabs. Everything saves automatically.

\---

## Quick Start

**Requirements:** Java 17 or later

```
java -jar MyCelia.jar
```

That's it. MyCelia creates its data files on first launch and loads sample data so you can get your bearings immediately.

\---

## The Interface

MyCelia can be used entirely by keyboard, but several actions are also accessible through the UI directly.

### UI Tabs and Buttons

**Action tabs:**

![UI Tabs and Buttons](docs/images/action-tabs.png)

|Tab|What it does|
|-|-|
|**Help**|Opens the help window — same as typing `help`|
|**Exit**|Saves all data and closes MyCelia — same as typing `exit`|

**Command terminal**:

![UI Tabs and Buttons](docs/images/command-terminal.png)

|Box|What it does|
|-|-|
|**Input**|Allows you to type in commands|
|**Response**|Displays response by system in response to commands|



**Book tabs**:

![UI Tabs and Buttons](docs/images/book-tabs.png)

|Tab|What it does|
|-|-|
|**Companies**|Switches to the Company Book — same as typing `switch` from the Delivery Book|
|**Deliveries**|Switches to the Delivery Book — same as typing `switch` from the Company Book|

Every UI action has an equivalent command. Use whichever feels faster for your workflow.

### Help Window

![Help Window](docs/images/help-window.png)

The help window provides a link to this user guide.

### Company Book View

![Company Book](docs/images/company-book.png)

The Company Book view shows all your business contacts. Each entry displays the company name, phone number, email, address, and any tags assigned to it. Below is the location the data is saved.

### Delivery Book View

![Delivery Book](docs/images/delivery-book.png)

The Delivery Book view shows all logged deliveries. Deliveries marked as delivered will display a `delivered` tag. Below is the location the data is saved.

\---

## Commands

### Navigating Between Books

|Command|What it does|
|-|-|
|`switch`|Toggle between Company Book and Delivery Book|
|`help`|Open the help window|
|`exit`|Save and close|

\---

### Company Book

Manage your network of business contacts. These commands are active when you're in the Company Book.

|Command|Format|Example|
|-|-|-|
|Add|`add n/NAME p/PHONE e/EMAIL a/ADDRESS \[t/TAG]...`|`add n/Acme Supplies p/62223333 e/hi@acme.com a/10 Anson Road t/supplier`|
|Edit|`edit INDEX \[n/NAME] \[p/PHONE] \[e/EMAIL] \[a/ADDRESS] \[t/TAG]...`|`edit 2 p/65559999 e/new@acme.com`|
|Delete|`delete INDEX`|`delete 3`|
|Find|`find KEYWORD \[MORE\_KEYWORDS]...`|`find acme tech logistics`|
|List all|`list`|`list`|
|Clear all|`clear`|`clear`|

**Company prefixes:**

|Prefix|Field|Required|
|-|-|-|
|`n/`|Company name|Yes|
|`p/`|Phone number|Yes|
|`e/`|Email address|Yes|
|`a/`|Physical address|Yes|
|`t/`|Tag (repeatable)|No|

\---

### Delivery Book

Track outgoing deliveries. Use `switch` or the Deliveries tab to get here from the Company Book.

|Command|Format|Example|
|-|-|-|
|Add|`add pr/PRODUCT c/COMPANY a/ADDRESS \[t/TAG]...`|`add pr/Industrial Printer c/Acme Supplies a/10 Anson Road t/urgent`|
|Edit|`edit INDEX \[pr/PRODUCT] \[c/COMPANY] \[a/ADDRESS] \[t/TAG]...`|`edit 1 a/20 Harbour Front Walk t/fragile`|
|Delete|`delete INDEX`|`delete 2`|
|Mark delivered|`mark INDEX`|`mark 1`|
|Unmark|`unmark INDEX`|`unmark 1`|
|Find|`find KEYWORD \[MORE\_KEYWORDS]...`|`find printer laptop`|
|Clear all|`clear`|`clear`|

**Delivery prefixes:**

|Prefix|Field|Required|
|-|-|-|
|`pr/`|Product name|Yes|
|`c/`|Company name|Yes|
|`a/`|Delivery address|Yes|
|`t/`|Tag (repeatable)|No|

\---

## How Data Works

* All data saves automatically after every command — no manual save needed
* Company records live in `addressbook.json`
* Delivery records live in `deliverybook.json`
* Both files are created in the same folder as the jar on first launch
* To back up, copy both JSON files somewhere safe
* To transfer to another machine, move the jar and both JSON files together

\---

## Project Structure

```
src/
├── main/java/seedu/address/
│   ├── logic/
│   │   ├── commands/
│   │   │   ├── companycommands/   # add, edit, delete, find, list, clear, switch
│   │   │   ├── deliverycommands/  # add, edit, delete, mark, unmark, find, clear
│   │   │   └── uicommand/         # help, exit
│   │   └── parser/
│   │       ├── companyparser/
│   │       └── deliveryparser/
│   ├── model/
│   │   ├── company/               # Company, Name, Phone, Email, Address
│   │   └── delivery/              # Delivery, Product, Company, Address
│   ├── storage/                   # JSON persistence for both books
│   └── ui/                        # JavaFX interface components
```

\---

## Built With

* Java 17
* JavaFX
* Jackson (JSON serialisation)
* JUnit 5 (testing)

\---

*MyCelia — named after mycelium, the underground network that keeps fungi connected. Because your business relationships deserve the same.*


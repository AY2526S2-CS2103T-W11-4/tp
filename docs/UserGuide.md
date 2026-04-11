[![CI Status](https://github.com/se-edu/addressbook-level3/workflows/Java%20CI/badge.svg)](https://github.com/AY2526S2-CS2103T-W11-4/tp)

> _Keeping your business network connected_

MyCelia is a command-line-driven desktop application for B2B delivery coordinators. Manage your business contacts and track outgoing deliveries — all from a single keyboard-driven interface. No clicking around. Just type and go.

<img src="images/ui-overview.png" width="700" alt="MyCelia Main Window" />

---

## What's Inside

MyCelia runs two books simultaneously:

* **Company Book** — your directory of business partners, suppliers, and clients
* **Delivery Book** — your log of outgoing deliveries linked to those companies

Switch between them with a single command or via the UI tabs. Everything saves automatically.

---

## Quick Start

**Requirements:** Java 17 or later

```
java -jar MyCelia.jar
```

That's it. MyCelia creates its data files on first launch and loads sample data so you can get your bearings immediately.

---

## The Interface

MyCelia can be used entirely by keyboard, but several actions are also accessible through the UI directly.

### Navigation Bar

<img src="images/navigation-bar.png" width="500" alt="Navigation Bar" />

At the top of the window is a pill toggle bar with navigation buttons:

|Button|What it does|
|-|-|
|**Companies**|Switch to the Company Book — same as typing `switch` from the Delivery Book|
|**Deliveries**|Switch to the Delivery Book — same as typing `switch` from the Company Book|
|**Routes**|Switch to the Routes tab to view route plans|
|**Help**|Open the help window — same as typing `help`|

### Command Terminal

<img src="images/command-terminal.png" width="600" alt="Command Terminal" />

|Box|What it does|
|-|-|
|**Input**|Type commands here. A hint line below shows the expected format as you type, and a dropdown suggests matching commands.|
|**Response**|Displays the system's response to your last command|

Switching tabs clears the command input, since commands are tab-specific.

Every UI action has an equivalent command. Use whichever feels faster for your workflow.

### Help Window

<img src="images/help-window.png" width="500" alt="Help Window" />

The help window provides a link to this user guide.

### Company Book View

<img src="images/company-book.png" width="600" alt="Company Book" />

The Company Book view shows all your business contacts. Each entry displays the company name, phone number, email, address, and any tags assigned to it. Below is the location the data is saved.

### Delivery Book View

<img src="images/delivery-book.png" width="600" alt="Delivery Book" />

The Delivery Book view shows all logged deliveries. Deliveries marked as delivered will display a `delivered` tag. You can check individual deliveries using their checkboxes to select them for route planning. A **Plan Today's Route** button at the top of the list becomes active when at least one delivery is selected — clicking it opens the Routes tab and plans the route. Below is the location the data is saved.

### Routes View

<img src="images/routes-view.png" width="700" alt="Routes View" />

The Routes view displays an interactive map with the optimised route for selected deliveries. Switch to it using the **Routes** pill button, or trigger it automatically via the `route` command or the **Plan Today's Route** button.

---

## Commands

### Notes about command format

* Words in `UPPER_CASE` are parameters to be supplied by the user.
* Items in square brackets are optional.
* Parameters can be provided in any order unless otherwise stated.
* For commands that do not require parameters (e.g. `help`, `switch`, `exit`), any additional trailing input will be ignored.

### Global Commands

These commands work in both the Company Book and Delivery Book.

|Command|Format|What it does|
|-|-|-|
|`switch`|`switch`|Toggle between Company Book and Delivery Book|
|`set`|`set a/ADDRESS`|Set your delivery origin address (used as the starting point for route planning)|
|`help`|`help`|Open the help window|
|`exit`|`exit`|Save and close|

---

### Company Book

Manage your network of business contacts. These commands are active when you're in the Company Book.

|Command|Format|Example|
|-|-|-|
|Add|`add n/NAME p/PHONE e/EMAIL a/ADDRESS \[t/TAG]...`|`add n/Acme Supplies p/62223333 e/hi@acme.com a/10 Anson Road t/supplier`|
|Edit|`edit INDEX \[n/NAME] \[p/PHONE] \[e/EMAIL] \[a/ADDRESS] \[t/TAG]...`|`edit 2 p/65559999 e/new@acme.com`|
|Delete|`delete INDEX`|`delete 3`|
|Filter|`filter [c/NAME] [a/ADDRESS] [p/PHONE] [e/EMAIL] [t/TAG]...`|`filter c/Dell t/important`|
|Clear filter|`unfilter`|`unfilter`|
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

**Filter prefixes:**

|Prefix|Meaning|
|-|-|
|`c/`|Company name contains keyword|
|`a/`|Address contains keyword|
|`p/`|Phone number contains digits|
|`e/`|Email contains keyword|
|`t/`|Tag matches keyword|

#### Notes

* Company names may contain only alphanumeric English characters and spaces.
* Addresses currently support only standard English keyboard characters.
* Tags may contain only alphanumeric English characters.
* Specifying one or more `t/` fields in an `edit` command replaces all existing tags.

---

### Delivery Book

Track outgoing deliveries. Use `switch` or the Deliveries tab to get here from the Company Book.

When adding or editing a delivery, the specified company must already exist in the Company Book.  
If no matching company is found, the command will fail.  
The delivery is linked directly to the existing company record instead of storing a separate company name string.

|Command|Format|Example|
|-|-|-|
|Add|`add p/PRODUCT c/COMPANY d/DEADLINE [t/TAG]...`|`add p/Industrial Printer c/Acme Supplies d/2026-03-25 14:30 t/urgent`|
|Delete|`delete INDEX`|`delete 2`|
|Edit|`edit INDEX [p/PRODUCT] [c/COMPANY] [d/DEADLINE] [t/TAG]...`|`edit 1 d/2026-03-26 09:00 t/fragile`|
|Mark delivered|`mark INDEX`|`mark 1`|
|Unmark|`unmark INDEX`|`unmark 1`|
|Select for routing|`select INDEX [INDEX]...`|`select 1 3 5`|
|Clear selection|`select none`|`select none`|
|Plan route|`route`|`route`|
|Filter|`filter [p/PRODUCT] [c/COMPANY] [d/DEADLINE] [t/TAG]...`|`filter c/Dell p/Laptop t/fragile`|
|Reset filter|`unfilter`|`unfilter`|
List all|`list`|`list`|
|Sort company by deadline|`sort c/COMPANY`|`sort c/Acme Supplies`|
|Clear all|`clear`|`clear`|

**Delivery prefixes:**

|Prefix|Field|Required|
|-|-|-|
|`p/`|Product name|Yes|
|`c/`|Company name|Yes|
|`d/`|Deadline (`yyyy-MM-dd HH:mm`)|Yes for `add`, optional for `edit`|
|`t/`|Tag (repeatable)|No|

#### Notes

* The company specified in `c/COMPANY` must already exist in the Company Book.
* Company matching is case-insensitive.
* Deadlines must follow the format `yyyy-MM-dd HH:mm`.
* A duplicate delivery cannot be added.

Deliveries are sorted by deadline in ascending order, so the earliest deadline appears first.
**Notes on specific commands:**

- **`list`** — shows all deliveries and re-sorts the entire list by deadline (earliest first). Use this to reset after a `find` or `sort` filter.
- **`sort c/COMPANY`** — filters to a specific company's deliveries and sorts them by deadline. The company name must match exactly (case-insensitive). Use `list` to return to the full view.
- **`select INDEX [INDEX]...`** — checks the deliveries at those indices for route planning. Repeating an index has no effect. Use `select none` to clear the entire selection. Individual deliveries can also be checked/unchecked via their checkboxes in the list.
- **`route`** — opens the Routes tab and plans the optimised route for all currently selected deliveries. Requires at least one delivery to be selected. Equivalent to clicking **Plan Today's Route**.

---

## How Data Works

* All data saves automatically after every command — no manual save needed
* Company records live in `addressbook.json`
* Delivery records live in `deliverybook.json`
* Both files are created in the same folder as the jar on first launch
* To back up, copy both JSON files somewhere safe
* To transfer to another machine, move the jar and both JSON files together

---

## Project Structure

```
src/
├── main/java/seedu/address/
│   ├── logic/
│   │   ├── commands/
│   │   │   ├── companycommands/   # add, edit, delete, list, clear, switch, filter, unfilter
│   │   │   ├── deliverycommands/  # add, edit, delete, mark, unmark, list, clear, select, sort, route, switch, filter, unfilter
│   │   │   ├── SetCommand.java    # set (shared — available in both modes)
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

---

## Built With

* Java 17
* JavaFX
* Jackson (JSON serialisation)
* JUnit 5 (testing)

---

*MyCelia — named after mycelium, the underground network that keeps fungi connected. Because your business relationships deserve the same.*


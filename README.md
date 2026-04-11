# MyCelia

MyCelia is a command-line-driven desktop application for B2B delivery coordinators. It extends the original AB3 into a focused dual-mode system for managing business contacts and outgoing deliveries in one place.

![MyCelia Main Window](docs/images/ui-overview.png)

## Key Features

- Maintain a Company Book for business partners, suppliers, and clients
- Maintain a Delivery Book for outgoing deliveries linked to companies
- Switch between company and delivery workflows using commands or UI tabs
- Use keyboard-first commands for common operations such as add, edit, delete, find, mark, and unmark
- Save data automatically in JSON files

## Quick Start

Requirements: Java 17 or later

```bash
java -jar MyCelia.jar
```

On first launch, MyCelia creates its data files and loads sample data for both books.

## Documentation

- [User Guide](docs/UserGuide.md)
- [Developer Guide](docs/DeveloperGuide.md)

## Built With

- Java 17
- JavaFX
- Jackson
- JUnit 5

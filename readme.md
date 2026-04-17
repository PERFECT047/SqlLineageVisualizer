# 🧠 SQL Lineage Visualizer

A full-stack database schema visualization tool that automatically extracts relational metadata from a live database and renders it into interactive ER diagrams using multiple visualization engines.

---

## 🚀 Features

- 🔍 **Automatic Schema Extraction**
    - Connect to any JDBC-compatible database
    - Extract tables, columns, and relationships

- 🧬 **Schema Enrichment**
    - Detect Primary Keys (PK)
    - Detect Foreign Keys (FK)
    - Normalize metadata for visualization

- 🎨 **Multiple Visualization Engines**
    - Graphviz (best quality, HTML table layout)
    - Mermaid (lightweight ER diagrams)
    - PlantUML (enterprise-style diagrams)
    - D2 (modern diagramming)

- ⚡ **Kroki Integration**
    - Uses POST-based rendering (avoids URL length limits)
    - Returns raw SVG for all engines

- 🖥️ **Interactive Frontend**
    - Pan & zoom (via `svg-pan-zoom`)
    - SVG + PNG download
    - Clean UI with validation & loading states

---

## 🏗️ Architecture

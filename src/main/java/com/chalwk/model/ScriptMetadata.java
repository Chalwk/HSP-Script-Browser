/**
 * HSP-Script-Browser
 * Copyright (c) 2025 Jericho Crosby (Chalwk)
 * <p>
 * This project is licensed under the MIT License.
 * See LICENSE file for details:
 * https://github.com/Chalwk/HSP-Script-Browser/blob/main/LICENSE
 */

package com.chalwk.model;

public class ScriptMetadata {
    private String title;
    private String shortDescription;
    private String description;
    private String filename;
    private ScriptCategory category;

    public ScriptMetadata() {
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public ScriptCategory getCategory() {
        return category;
    }

    public void setCategory(ScriptCategory category) {
        this.category = category;
    }

    public String getGitHubUrl() {
        return "https://github.com/Chalwk/HALO-SCRIPT-PROJECTS/blob/master/sapp/" +
                category.getFolderName() + "/" + filename;
    }

    public String getRawScriptUrl() {
        return "https://raw.githubusercontent.com/Chalwk/HALO-SCRIPT-PROJECTS/master/sapp/" +
                category.getFolderName() + "/" + filename;
    }

    public String getFilenameWithoutExtension() {
        if (filename == null) return null;
        return filename.replace(".lua", "");
    }
}
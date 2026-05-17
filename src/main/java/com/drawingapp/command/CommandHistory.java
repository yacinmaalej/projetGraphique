package com.drawingapp.command;

import com.drawingapp.logger.Logger;
import com.drawingapp.logger.LoggerManager;

import java.util.Stack;

public class CommandHistory {
    private Stack<Command> undoStack;
    private Stack<Command> redoStack;
    private Logger logger;

    public CommandHistory() {
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
        this.logger = LoggerManager.getInstance().getLogger();
        logger.log("CommandHistory initialisé - Prêt pour Undo/Redo");
    }

    public void executeCommand(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear(); // ★ Vider le redo stack quand une nouvelle action est faite
        logger.log("CommandHistory: Commande exécutée - " +
                command.getDescription() +
                " | undoStack=" + undoStack.size() +
                " | redoStack=" + redoStack.size());
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop();
            command.undo();
            redoStack.push(command);
            logger.log("CommandHistory: UNDO - " + command.getDescription() +
                    " | undoStack=" + undoStack.size() +
                    " | redoStack=" + redoStack.size());
        } else {
            logger.log("CommandHistory: Rien à undo - undoStack est vide");
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            Command command = redoStack.pop();
            command.redo();
            undoStack.push(command);
            logger.log("CommandHistory: REDO - " + command.getDescription() +
                    " | undoStack=" + undoStack.size() +
                    " | redoStack=" + redoStack.size());
        } else {
            logger.log("CommandHistory: Rien à redo - redoStack est vide");
        }
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public int getUndoStackSize() {
        return undoStack.size();
    }

    public int getRedoStackSize() {
        return redoStack.size();
    }

    public void clear() {
        undoStack.clear();
        redoStack.clear();
        logger.log("CommandHistory: Historique effacé");
    }
}
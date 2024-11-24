/*
 * InvestBook
 * Copyright (C) 2024  Spacious Team <spacious-team@ya.ru>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.investbook.report.html;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExcelFormulaEvaluatorHelperTest {

    @Test
    void testEvaluateFormulaCells() {
        // Setup
        HSSFWorkbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Test Sheet");
        Row row = sheet.createRow(0);
        Cell formulaCell = row.createCell(0);
        formulaCell.setCellFormula("SUM(1, 2)");

        // Act
        ExcelFormulaEvaluatorHelper.evaluateFormulaCells(workbook);

        // Assert
        assertEquals(CellType.NUMERIC, formulaCell.getCachedFormulaResultType());
        assertEquals(3.0, formulaCell.getNumericCellValue());
    }

    @Test
    void testHandleNotImplementedException_withIFERROR() {
        // Setup
        HSSFWorkbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Test Sheet");
        Row row = sheet.createRow(0);
        Cell formulaCell = row.createCell(0);
        formulaCell.setCellFormula("IFERROR(1/0, 42)");

        HSSFFormulaEvaluator evaluator = mock(HSSFFormulaEvaluator.class);

        // Mock behavior to throw NotImplementedException
        doThrow(new NotImplementedException("Mocked")).when(evaluator).evaluateFormulaCell(any(Cell.class));

        // Act
        boolean result = ExcelFormulaEvaluatorHelper.handleNotImplementedException(formulaCell, evaluator);

        // Assert
        assertTrue(result);
        assertEquals("42", formulaCell.getCellFormula());
    }

    @Test
    void testHandleNotImplementedException_invalidFormula() {
        // Setup
        HSSFWorkbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Test Sheet");
        Row row = sheet.createRow(0);
        Cell formulaCell = row.createCell(0);
        formulaCell.setCellFormula("INVALIDFORMULA(1, 2)");

        HSSFFormulaEvaluator evaluator = mock(HSSFFormulaEvaluator.class);

        // Mock behavior to throw NotImplementedException
        doThrow(new NotImplementedException("Mocked")).when(evaluator).evaluateFormulaCell(any(Cell.class));

        // Act
        boolean result = ExcelFormulaEvaluatorHelper.handleNotImplementedException(formulaCell, evaluator);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIndexOfCloseBrace() {
        String formula = "IF(SUM(1, 2), 42)";
        int openBracePos = formula.indexOf("(");

        // Act
        int closeBracePos = ExcelFormulaEvaluatorHelper.indexOfCloseBrace(formula, openBracePos);

        // Assert
        assertEquals(formula.lastIndexOf(")"), closeBracePos);
    }

    @Test
    void testIndexOfCloseBrace_unbalancedBraces() {
        String formula = "IF(SUM(1, 2)";
        int openBracePos = formula.indexOf("(");

        // Act
        int closeBracePos = ExcelFormulaEvaluatorHelper.indexOfCloseBrace(formula, openBracePos);

        // Assert
        assertEquals(-1, closeBracePos);
    }

    @Test
    void testIndexOfSecondArg_noSecondArg() {
        String formula = "IF(SUM(1, 2))";
        int openBracePos = formula.indexOf("(");

        // Act
        int secondArgPos = ExcelFormulaEvaluatorHelper.indexOfSecondArg(formula, openBracePos);

        // Assert
        assertEquals(-1, secondArgPos);
    }
}

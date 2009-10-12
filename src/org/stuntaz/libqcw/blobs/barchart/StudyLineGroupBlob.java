/*
 * libqcw - A library for parsing, manipulating, and writing QCharts (TM) 
 * workspace files.
 *
 * Copyright (C) 2008-2009 Jon Nall
 * QCharts is a registered service mark of eSignal, Inc.
 * RagingBull is a registered service mark of eSignal, Inc.
 *
 * Licensed under the Open Software License version 3.0 (the "License"); you
 * may not use this file except in compliance with the License. You should
 * have received a copy of the License along with this software; if not, you
 * may obtain a copy of the License at
 *
 * http://opensource.org/licenses/osl-3.0.php
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.stuntaz.libqcw.blobs.barchart;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.stuntaz.libqcw.IBarChartVisitor;
import org.stuntaz.libqcw.QCWException;
import org.stuntaz.libqcw.blobs.QDword;
import org.stuntaz.libqcw.blobs.QRecord;
import org.stuntaz.libqcw.blobs.QUtils;

/**
 * Represents a group of lines associated with a bar chart study.
 * 
 * @author nall
 *
 */
public final class StudyLineGroupBlob
    extends QRecord
{
    private final List<SymbolEntryBlob> symbols = new ArrayList<SymbolEntryBlob>();

    /**
     * Return the symbol entries for this study.
     * 
     * @return the list of symbol-specific lines and notes
     */
    public List<SymbolEntryBlob> getSymbols()
    {
        return symbols;
    }

    /**
     * Adds a symbol entry to this study
     * 
     * @param symbol the symbol entry to add
     */
    public void addSymbolEntry(final SymbolEntryBlob symbol)
    {
        for (final SymbolEntryBlob s : symbols)
        {
            if (s.getSymbolName().equals(symbol.getSymbolName()))
            {
                throw new QCWException("Cannot add symbol " + s.getSymbolName()
                    + " to lines group since an entry already exists");
            }
        }

        symbols.add(symbol);
    }

    /**
     * Remove the specified symbol from this study's symbol entries. Note that
     * {@code symName} must be the fully qualified symbol name (e.g. INDEX:OEX.X).
     * 
     * @param symName the name of the symbol to remove
     */
    public void removeSymbol(final String symName)
    {
        final Set<Integer> idxToRemove = new HashSet<Integer>();
        for (int i = 0; i < symbols.size(); ++i)
        {
            if (symbols.get(i).getSymbolName().equals(symName))
            {
                idxToRemove.add(i);
            }
        }

        assert (idxToRemove.size() <= 1);
        for (final int i : idxToRemove)
        {
            symbols.remove(i);
        }
    }

    @Override
    protected int getInternalSize()
    {
        int totalBytes = QUtils.getSize(QDword.class); // symbols.size()

        for (final SymbolEntryBlob symbol : symbols)
        {
            System.out.println("Add size for symbol " + symbol.getSymbolName()
                + ": " + symbol.getSize());
            totalBytes += symbol.getSize();
        }
        return totalBytes;
    }

    @Override
    public void write(final OutputStream output)
        throws IOException
    {
        super.write(output);

        QUtils.writeQDword(symbols.size(), output);
        for (final SymbolEntryBlob symbol : symbols)
        {
            System.out.println("Writing symbol: " + symbol.getSymbolName());
            symbol.write(output);
        }
    }

    public void parse(final InputStream stream)
        throws IOException
    {
        final int size = parseRecordHeader(stream);

        final int numSyms = QUtils.readQDword(stream);

        for (int i = 0; i < numSyms; ++i)
        {
            final SymbolEntryBlob sym = new SymbolEntryBlob(stream);
            symbols.add(sym);
        }

        setValid();
        assert (size == getInternalSize());
    }

    /**
     * Accept the specified bar chart visitor.
     * 
     * @param visitor the visitor to accept
     */
    public void accept(final IBarChartVisitor visitor)
    {
        visitor.visit(this);
    }
}

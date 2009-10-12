#!/usr/bin/env perl -w

use strict;

sub removeOldHeader()
{
    my $file = shift;
    open(FILE, "<$file") or die("Cannot open $file for reading\n");
    open(OUTFILE, ">${file}.tmp") or die("Cannot open ${file}.tmp for writing\n");

    my $inHeader = 1;
    my $inBlock = 0;
    my $sawFirstRealLine = 0;
    while(<FILE>)
    {
        my $line = $_;

        if($inHeader == 1)
        {
            if($line =~ /^\/\//)
            {
                # remove it
            }
            elsif($line =~ /^\/\*/)
            {
                # start of block comment
                $inBlock = 1;
            }
            elsif($line =~ /^ \*\//)
            {
                # end of block comment
                $inHeader = 0;
                $inBlock = 0;
            }
            elsif($inBlock == 1)
            {
                # ignore it
            }
            else
            {
                print OUTFILE $line;
                $inHeader = 0;
            }
        }
        elsif($sawFirstRealLine == 0 && $line !~ /\S/)
        {
            # suck up blank lines before code
        }
        else
        {
            $sawFirstRealLine = 1;
            print OUTFILE $line;
        }
    }

    close(OUTFILE);
    close(FILE);
    system("mv ${file}.tmp $file");
}

sub addNewHeader()
{
    my $file = shift;
    open(FILE, "<$file") or die("Cannot open $file for reading\n");
    open(OUTFILE, ">${file}.tmp") or die("Cannot open ${file}.tmp for writing\n");

    print OUTFILE <<EOF;
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

EOF
    while(<FILE>)
    {
        my $line = $_;
        print OUTFILE $line;
    }

    close(OUTFILE);
    close(FILE);
    system("mv ${file}.tmp $file");
}


sub main()
{
    my @files = `find . -name \*.java`;

    for my $file (@files)
    {
        chomp($file);
        &removeOldHeader($file);
        &addNewHeader($file);
    }
}

main();

package dan200.computercraft.core.terminal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class TerminalTest
{
    private static class MockOnChangedCallback implements Runnable {
        private int timesCalled = 0;

        @Override
        public void run()
        {
            timesCalled++;
        }

        public void assertNotCalled() {
            assertEquals(0, timesCalled, "onChanged callback should not have been called");
        }

        public void assertCalled() {
            assertTrue(timesCalled > 0, "onChanged callback should have been called");
        }

        public void assertCalledTimes(int expectedTimesCalled) {
            assertEquals(expectedTimesCalled, timesCalled, "onChanged callback was not called the correct number of times");
        }
    }

    private static class TerminalTestHelper {
        private final List<String> textLines;
        private final List<String> textColourLines;
        private final List<String> backgroundColourLines;

        public TerminalTestHelper( Terminal terminal ) {
            textLines = new ArrayList<>( terminal.getHeight() );
            textColourLines = new ArrayList<>( terminal.getHeight() );
            backgroundColourLines = new ArrayList<>(terminal.getHeight());

            for ( int i = 0; i < terminal.getHeight(); i++ ) {
                textLines.add(terminal.getLine(i).toString());
                textColourLines.add(terminal.getTextColourLine(i).toString());
                backgroundColourLines.add(terminal.getBackgroundColourLine(i).toString());
            }
        }

        public TerminalTestHelper assertTextMatches( String[] snapshot ) {
            List<String> snapshotLines = new ArrayList<>(Arrays.asList(snapshot));
            assertLinesMatch(snapshotLines, textLines);
            return this;
        }

        public TerminalTestHelper assertTextColourMatches( String[] snapshot ) {
            List<String> snapshotLines = new ArrayList<>(Arrays.asList(snapshot));
            assertLinesMatch(snapshotLines, textColourLines);
            return this;
        }

        public TerminalTestHelper assertBackgroundColourMatches( String[] snapshot ) {
            List<String> snapshotLines = new ArrayList<>(Arrays.asList(snapshot));
            assertLinesMatch(snapshotLines, backgroundColourLines);
            return this;
        }
    }

    @Test
    void testCreation()
    {
        Terminal terminal = new Terminal( 16, 9 );
        assertEquals( 16, terminal.getWidth() );
        assertEquals( 9, terminal.getHeight() );
    }

    @Test
    void testSetAndGetLine()
    {
        Terminal terminal = new Terminal( 16, 9 );
        terminal.setLine(1, "ABCDEFGHIJKLMNOP", "0123456789abcdef", "fedcba9876543210");
        assertEquals( "ABCDEFGHIJKLMNOP", terminal.getLine(1).toString() );
        assertEquals( "0123456789abcdef", terminal.getTextColourLine(1).toString() );
        assertEquals( "fedcba9876543210", terminal.getBackgroundColourLine(1).toString() );
    }

    @Test
    void testDefaults()
    {
        Terminal terminal = new Terminal( 16, 9 );
        assertEquals( 0, terminal.getCursorX() );
        assertEquals( 0, terminal.getCursorY() );
        assertFalse(terminal.getCursorBlink());
        assertEquals(0, terminal.getTextColour());
        assertEquals(15, terminal.getBackgroundColour());
    }

    @Test
    void testDefaultTextBuffer()
    {
        new TerminalTestHelper(new Terminal(4, 3))
            .assertTextMatches( new String[] {
                "    ",
                "    ",
                "    ",
            } );
    }

    @Test
    void testDefaultTextColourBuffer()
    {
        new TerminalTestHelper(new Terminal(4, 3))
            .assertTextColourMatches( new String[] {
                "0000",
                "0000",
                "0000",
            } );
    }

    @Test
    void testDefaultBackgroundColourBuffer()
    {
        new TerminalTestHelper(new Terminal(4, 3))
            .assertBackgroundColourMatches( new String[] {
                "ffff",
                "ffff",
                "ffff",
            } );
    }

    @Test
    void testResizeHeight()
    {
        MockOnChangedCallback mockOnChangedCallback = new MockOnChangedCallback();
        Terminal terminal = new Terminal(4, 3, mockOnChangedCallback);
        terminal.resize(4, 5);

        new TerminalTestHelper( terminal )
            .assertTextMatches( new String[] {
                "    ",
                "    ",
                "    ",
                "    ",
                "    ",
            } ).assertTextColourMatches( new String[] {
                "0000",
                "0000",
                "0000",
                "0000",
                "0000",
            } ).assertBackgroundColourMatches( new String[] {
                "ffff",
                "ffff",
                "ffff",
                "ffff",
                "ffff",
            } );

        mockOnChangedCallback.assertCalledTimes(1);
    }

    @Test
    void testResizeWidth()
    {
        MockOnChangedCallback mockOnChangedCallback = new MockOnChangedCallback();
        Terminal terminal = new Terminal(4, 3, mockOnChangedCallback);
        terminal.resize(6, 3);

        new TerminalTestHelper( terminal )
            .assertTextMatches( new String[] {
                "      ",
                "      ",
                "      ",
            } ).assertTextColourMatches( new String[] {
                "000000",
                "000000",
                "000000",
            } ).assertBackgroundColourMatches( new String[] {
                "ffffff",
                "ffffff",
                "ffffff",
            } );

        mockOnChangedCallback.assertCalledTimes(1);
    }

    @Test
    void testResizeWidthAndHeight()
    {
        MockOnChangedCallback mockOnChangedCallback = new MockOnChangedCallback();
        Terminal terminal = new Terminal(4, 3, mockOnChangedCallback);
        terminal.resize(6, 4);

        new TerminalTestHelper( terminal )
        .assertTextMatches( new String[] {
            "      ",
            "      ",
            "      ",
            "      ",
        } ).assertTextColourMatches( new String[] {
            "000000",
            "000000",
            "000000",
            "000000",
        } ).assertBackgroundColourMatches( new String[] {
            "ffffff",
            "ffffff",
            "ffffff",
            "ffffff",
        } );

        mockOnChangedCallback.assertCalledTimes(1);
    }

    @Test
    void testResizeSmaller()
    {
        MockOnChangedCallback mockOnChangedCallback = new MockOnChangedCallback();
        Terminal terminal = new Terminal(4, 3, mockOnChangedCallback);
        terminal.resize(2, 2);

        new TerminalTestHelper( terminal )
        .assertTextMatches( new String[] {
            "  ",
            "  ",
        } ).assertTextColourMatches( new String[] {
            "00",
            "00",
        } ).assertBackgroundColourMatches( new String[] {
            "ff",
            "ff",
        } );

        mockOnChangedCallback.assertCalledTimes(1);
    }

    @Test
    void testResizeWithSameDimensions()
    {
        MockOnChangedCallback mockOnChangedCallback = new MockOnChangedCallback();
        Terminal terminal = new Terminal(4, 3, mockOnChangedCallback);
        terminal.resize(4, 3);

        new TerminalTestHelper( terminal )
        .assertTextMatches( new String[] {
            "    ",
            "    ",
            "    ",
        } ).assertTextColourMatches( new String[] {
            "0000",
            "0000",
            "0000",
        } ).assertBackgroundColourMatches( new String[] {
            "ffff",
            "ffff",
            "ffff",
        } );

        mockOnChangedCallback.assertNotCalled();
    }
}
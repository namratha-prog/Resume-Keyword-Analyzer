import java.awt.*;
import java.io.PrintWriter;
import java.util.*;
import javax.swing.*;

public class ResumeJDAnalyzerFinal {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Resume vs JD Keyword Analyzer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 850);
        frame.setLayout(new BorderLayout(10, 10));

        // ==== JD Input Area ====
        JTextArea jdArea = new JTextArea(8, 70);
        jdArea.setLineWrap(true);
        jdArea.setWrapStyleWord(true);
        JScrollPane jdScroll = new JScrollPane(jdArea);
        jdScroll.setBorder(BorderFactory.createTitledBorder("Job Description"));

        // ==== Resume Input Area ====
        JTextArea resumeArea = new JTextArea(8, 70);
        resumeArea.setLineWrap(true);
        resumeArea.setWrapStyleWord(true);
        JScrollPane resumeScroll = new JScrollPane(resumeArea);
        resumeScroll.setBorder(BorderFactory.createTitledBorder("Resume Content"));

        // ==== Output Area (HTML with Scroll) ====
        JEditorPane outputArea = new JEditorPane();
        outputArea.setContentType("text/html");
        outputArea.setEditable(false);
        outputArea.setText("<html><body style='font-family: sans-serif;'>Results will be shown here</body></html>");
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setPreferredSize(new Dimension(950, 350));
        outputScroll.setBorder(BorderFactory.createTitledBorder("📊 Analysis Result"));

        // ==== Buttons ====
        JButton analyzeBtn = new JButton("Analyze");
        JButton clearBtn = new JButton("Clear");

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.add(jdScroll);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        inputPanel.add(resumeScroll);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(analyzeBtn);
        buttonPanel.add(clearBtn);

        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.add(outputScroll, BorderLayout.CENTER);

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.add(outputPanel, BorderLayout.SOUTH);

        // ==== Analyze Button Action ====
        analyzeBtn.addActionListener(e -> {
            String jdText = jdArea.getText().toLowerCase();
            String resumeText = resumeArea.getText().toLowerCase();

            Set<String> stopwords = new HashSet<>(Arrays.asList(
                "and", "or", "a", "the", "in", "with", "of", "to", "for", "is", "are", "on", "as", "by", "an", "at"
            ));

            Set<String> jdKeywords = new HashSet<>(Arrays.asList(jdText.split("[ ,.;:\\-\\n\\t]+")));
            Set<String> resumeWords = new HashSet<>(Arrays.asList(resumeText.split("[ ,.;:\\-\\n\\t]+")));

            jdKeywords.removeAll(stopwords);
            resumeWords.removeAll(stopwords);

            Set<String> matched = new TreeSet<>();
            Set<String> missing = new TreeSet<>(jdKeywords);

            for (String word : jdKeywords) {
                if (resumeWords.contains(word)) {
                    matched.add(word);
                }
            }

            missing.removeAll(matched);
            int matchCount = matched.size();
            double matchPercent = jdKeywords.isEmpty() ? 0 : ((double) matchCount / jdKeywords.size()) * 100;
            int resumeScore = (int) (matchPercent / 10);

            // ==== HTML Output ====
            StringBuilder html = new StringBuilder();
            html.append("<html><body style='font-family:Arial, sans-serif;'>");
            html.append("<h2 style='color:#2E86C1;'>📊 Resume - JD Keyword Match Report</h2><hr>");
            html.append("<b>Total JD Keywords:</b> ").append(jdKeywords.size()).append("<br>");
            html.append("<b style='color:green;'>✔ Matched:</b> ").append(matchCount).append("<br>");
            html.append(String.format("<b style='color:blue;'>📈 Match Percentage:</b> %.2f%%<br>", matchPercent));
            html.append("<b>⭐ Resume Score:</b> ").append(resumeScore).append(" / 10<hr>");

            html.append("<h3 style='color:green;'>✅ Matched Keywords:</h3><ul>");
            for (String word : matched) {
                html.append("<li>").append(word).append("</li>");
            }
            html.append("</ul>");

            html.append("<h3 style='color:red;'>❌ Missing Important Keywords:</h3><ul>");
            for (String word : missing) {
                html.append("<li>").append(word).append("</li>");
            }
            html.append("</ul><hr>");

            html.append("<h3>📝 Feedback:</h3>");
            if (matchPercent >= 70) {
                html.append("<p style='color:green;'>✅ Strong match! Your resume aligns well with the JD.</p>");
            } else if (matchPercent >= 40) {
                html.append("<p style='color:orange;'>⚠ Decent match. Consider improving alignment.</p>");
            } else {
                html.append("<p style='color:red;'>❌ Poor match. Needs significant improvement.</p>");
            }

            html.append("</body></html>");

            outputArea.setText(html.toString());
            outputArea.setCaretPosition(0);  // scroll to top

            // Optional: Save to file
            try (PrintWriter writer = new PrintWriter("resume_analysis.html")) {
                writer.println(html.toString());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "⚠ Failed to save output file.");
            }
        });

        // ==== Clear Button Action ====
        clearBtn.addActionListener(e -> {
            jdArea.setText("");
            resumeArea.setText("");
            outputArea.setText("<html><body style='font-family: sans-serif;'>Results will be shown here</body></html>");
        });

        frame.setVisible(true);
    }
}
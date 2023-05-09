package test;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import gui.component.FrameBar;
import gui.component.IPanel;

public class Money {
    private JFrame frame;
    private int money;
    private int[] targetArray;
    private Map<Integer, Integer> targetMap;
    private boolean isShowAll;
    private int showTimes;
    private List<IPanel> targetCards;

    private JLabel target;
    private JLabel balance;

    public Money() {
        money = 60;
        targetArray = new int[2];
        isShowAll = false;
        showTimes = 0;

        frame = new JFrame();
        IPanel content = new IPanel(new BorderLayout());
        IPanel north = new IPanel(new BorderLayout());
        FrameBar bar = new FrameBar(frame);
        IPanel top = new IPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setPreferredSize(new Dimension(400, 100));
        north.add(bar, BorderLayout.NORTH);
        north.add(top, BorderLayout.CENTER);
        content.add(north, BorderLayout.NORTH);
        int rows = 3;
        int cols = 3;
        IPanel center = new IPanel(new GridLayout(rows, cols));
        center.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        content.add(center, BorderLayout.CENTER);

        IPanel top1 = new IPanel();
        IPanel top2 = new IPanel();
        IPanel top3 = new IPanel();
        JTextField text = new JTextField(10);
        JButton bText = new JButton("Confirm");
        balance = new JLabel("Money: " + money);
        target = new JLabel("Target:" + Arrays.toString(targetArray));
        JLabel tip = new JLabel("10/turn");
        JButton retry = new JButton("Retry");
        JButton showAll = new JButton("Show All");
        top1.add(text);
        top1.add(bText);
        top2.add(balance);
        top2.add(target);
        top3.add(tip);
        top3.add(retry);
        top3.add(showAll);
        top.add(top1);
        top.add(top2);
        top.add(top3);

        initTarget();
        retry(center, rows, cols);

        frame.setContentPane(content);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        frame.setOpacity(0.1f);
        frame.pack();
        frame.setSize(400, 500);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(new ImageIcon("src\\gui\\icon\\card.png").getImage());
        frame.setVisible(true);

        retry.addActionListener(e -> {
            retry(center, rows, cols);
        });

        showAll.addActionListener(e -> {
            if (isShowAll || showTimes == 9) {
                return;
            }
            isShowAll = true;
            openCards();
            computeBonus();
        });
    }

    private void openCards() {
        targetCards.forEach(card -> {
            ((CardLayout) card.getLayout()).show(card, "back");
        });
    }

    private void computeBonus() {
        int sum = 0;
        if (targetMap.get(targetArray[0]) != null) {
            sum = sum + targetMap.get(targetArray[0]);
        } else if (targetMap.get(targetArray[1]) != null) {
            sum = sum + targetMap.get(targetArray[1]);
        }
        if (sum != 0) {
            money = money + sum;
            balance.setText("money: " + money);
            JOptionPane.showMessageDialog(frame, "congratulations! bonus: " + sum);
        } else {
            JOptionPane.showMessageDialog(frame, "what a pity! bonus: 0");
        }
    }

    private void retry(IPanel center, int rows, int cols) {
        targetCards = new ArrayList<>();
        isShowAll = false;
        showTimes = 0;
        money = money - 10;
        balance.setText("money: " + money);
        initTarget();
        targetMap = new HashMap<>();
        center.removeAll();
        for (int i = 1; i <= (rows * cols); i++) {
            IPanel gridPanel = initGrid(targetMap);
            center.add(gridPanel);
        }
        center.reload();
    }

    public static void main(String[] args) {
        new Money();
    }

    private IPanel initGrid(Map<Integer, Integer> map) {
        CardLayout cardLayout = new CardLayout();
        AtomicBoolean clicked = new AtomicBoolean(false);
        IPanel panel = new IPanel(cardLayout);
        targetCards.add(panel);
        Random random = new Random();
        int number = random.nextInt(100);
        int multiple = random.nextInt(10);
        int price = 10;
        if (multiple == 0 || multiple == 1) {
            price = 10;
        } else if (multiple == 2 || multiple == 3) {
            price = 30;
        } else if (multiple == 4 || multiple == 5) {
            price = 50;
        } else if (multiple == 6) {
            price = 100;
        } else if (multiple == 7) {
            price = 200;
        } else if (multiple == 8) {
            price = 500;
        } else if (multiple == 9) {
            price = 1000000;
        }
        if (map.get(number) != null) {
            int temp = map.get(number) + price;
            map.put(number, temp);
        } else {
            map.put(number, price);
        }

        IPanel back = new IPanel();
        back.setLayout(new BoxLayout(back, BoxLayout.Y_AXIS));
        back.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.gray, Color.gray));
        JLabel label1 = new JLabel("number: " + number);
        JLabel label2 = new JLabel("price: " + price);
        back.add(label1);
        back.add(label2);
        JPanel front = new JPanel();
        front.setBackground(Color.gray);
        panel.add(back, "back");
        panel.add(front, "front");
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        cardLayout.show(panel, "front");

        front.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!clicked.get()) {
                    showTimes++;
                }
                clicked.set(true);
                cardLayout.show(panel, "back");
                if (showTimes == 9) {
                    computeBonus();
                }
            }
        });
        //return panel;
        return back;

    }

    /**
     * set 2 random target number
     */
    private void initTarget() {
        Random random = new Random();
        // 0-99
        targetArray[0] = random.nextInt(100);
        targetArray[1] = random.nextInt(100);
        target.setText("target:" + Arrays.toString(targetArray));
    }

}

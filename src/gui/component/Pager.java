package gui.component;

import common.model.event.impl.PagerEvent;
import common.model.observer.Observer;
import common.myenum.PageAction;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yty
 * @date 2024/4/29
 * @description
 */
public class Pager extends JPanel {
    private int totalCount; // 总条目数
    private AtomicInteger pageSize;
    // 每页显示条目数
    private int currentPage; // 当前页数
    private int currentIndex;//当前页标
    private int maxPage; //最大页码
    private JButton lastBtn; //上一页
    private JButton nextBtn; //下一页
    private JLabel totalLabel;
    private JPanel pagePanel;
    private JLabel currentLabel;//当前页码
    private JComboBox<Integer> sizeCombo;

    private int[] index; //显示7个当前页码
    private JLabel[] labels; //显示7个当前页码
    private List<Observer> observers;

    public Pager(int initTotalCount) {
        this.totalCount = initTotalCount;
        this.pageSize = new AtomicInteger(20);
        this.currentPage = 1; // 默认第一页
        this.currentIndex = 0;
        this.observers = new ArrayList<>();

        totalLabel = new JLabel("总共：" + totalCount);
        add(totalLabel);
        lastBtn = new JButton("上一页");
        nextBtn = new JButton("下一页");
        lastBtn.setBorder(BorderFactory.createRaisedBevelBorder());
        nextBtn.setBorder(BorderFactory.createRaisedBevelBorder());
        lastBtn.setBorderPainted(false);
        nextBtn.setBorderPainted(false);
        add(lastBtn);

        //initialize showing pages
        pagePanel = new JPanel();
        index = new int[7];
        labels = new JLabel[7];
        maxPage = totalCount % pageSize.get() != 0 ? totalCount / pageSize.get() + 1 : totalCount / pageSize.get();
        for (int i = 0; i < 7; i++) {
            if (i < maxPage) {
                int current = i + 1;
                index[i] = current;
                JLabel label = new JLabel(String.valueOf(current));
                if (current == currentPage) {
                    label.setForeground(Color.RED);
                    currentLabel = label;
                }
                labels[i] = label;
                pagePanel.add(label);
            } else {
                labels[i] = new JLabel();
            }
        }
        add(pagePanel);

        add(nextBtn);

        //page size
        Integer[] sizeRanges = new Integer[]{20, 50, 100, 200, 500, 1000};
        sizeCombo = new JComboBox<>(sizeRanges);
        add(sizeCombo);

        //listeners
        lastBtn.addActionListener(e -> {
            if (currentPage > 1) {
                if (index[6] > 7) {
                    for (int i = 6; i > 1; i--) {
                        int pageNum = index[i] - 1;
                        index[i] = pageNum;
                        labels[i].setText("" + pageNum);
                    }
                    if (index[2] == 3) {
                        index[1] = 2;
                        labels[1].setText("" + 2);
                    }
                }
                // if the last page  > 7, move forward an entire page and keep index unchanged;
                // if the last page  <= 7, move the index to the last index;
                currentPage--;
                currentIndex--;
                if (index[1] == -1) {
                    currentIndex = 6;
                }

                currentLabel.setForeground(Color.BLACK);
                JLabel last = labels[currentIndex];
                last.setForeground(Color.RED);
                currentLabel = last;
                pagePanel.repaint();


                //notify
                PagerEvent event = new PagerEvent(PageAction.LAST, currentPage, pageSize.get());
                notifyObserver(event);
            }
        });

        nextBtn.addActionListener(e -> {
            if (currentPage < maxPage) {
                if (currentPage > 6) {
                    //resize the pages
                    for (int i = 6, j = 0; i > 1; i--, j++) {
                        int pageNum = currentPage - j + 1;
                        index[i] = pageNum;
                        labels[i].setText("" + pageNum);
                    }
                    //hide the previous pages at the second location, -1 represents "..."
                    index[1] = -1;
                    labels[1].setText("...");
                }
                //move to the next index
                currentPage++;
                if (currentIndex != 6) {
                    currentIndex++;
                }
                currentLabel.setForeground(Color.BLACK);
                JLabel next = labels[currentIndex];
                next.setForeground(Color.RED);
                currentLabel = next;
                pagePanel.repaint();

                //notify
                PagerEvent event = new PagerEvent(PageAction.NEXT, currentPage, pageSize.get());
                notifyObserver(event);
            }
        });

        sizeCombo.addActionListener(e -> {
            Integer currentSize = (Integer) sizeCombo.getSelectedItem();
            if (currentSize != null && currentSize != this.pageSize.get()) {
                this.pageSize.set(currentSize);
                maxPage = totalCount % currentSize != 0 ? totalCount / currentSize + 1 : totalCount / currentSize;
                currentPage = 1;
                currentIndex = 0;
                //reset pages showing
                pagePanel.removeAll();
                for (int i = 0; i < 7; i++) {
                    if (i < maxPage) {
                        int current = i + 1;
                        index[i] = current;
                        labels[i].setText(String.valueOf(current));
                        if (current == currentPage) {
                            currentLabel.setForeground(Color.BLACK);
                            labels[i].setForeground(Color.RED);
                            currentLabel = labels[i];
                        }
                        pagePanel.add(labels[i]);
                    } else {
                        index[i] = 0;
                    }
                }
                pagePanel.revalidate();
                pagePanel.repaint();
            } else {
                this.pageSize.set(20);
            }
            //notify
            PagerEvent event = new PagerEvent(PageAction.SIZE, currentPage, pageSize.get());
            notifyObserver(event);
        });


    }

    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        this.observers.remove(observer);
    }

    private void notifyObserver(PagerEvent event) {
        for (Observer observer : observers) {
            observer.update(event);
        }
    }

    public void setTotalCount(int newTotalCount) {
        totalLabel.setText("总共：" + newTotalCount);
        //if maxPage changes, update the pages
        int curSize = pageSize.get();
        int newMaxPage = newTotalCount % curSize != 0 ? newTotalCount / curSize + 1 : newTotalCount / curSize;
        if (maxPage == newMaxPage) {
            return;
        }

        pagePanel.removeAll();
        //if maxPage decreases, we need to adjust the last current page if it exceeded boundaries.
        //etc.: maxPage: 4->2, currentPage: 4->2
        if (maxPage > newMaxPage && currentPage > newMaxPage) {
            currentPage = newMaxPage;
            currentIndex = currentPage - 1;
        }
        for (int i = 0; i < 7; i++) {
            if (i < newMaxPage) {
                int current = i + 1;
                index[i] = current;
                labels[i].setText(String.valueOf(current));
                if (current == currentPage) {
                    currentLabel.setForeground(Color.BLACK);
                    labels[i].setForeground(Color.RED);
                    currentLabel = labels[i];
                }
                pagePanel.add(labels[i]);
            } else {
                index[i] = 0;
            }
        }
        pagePanel.revalidate();
        pagePanel.repaint();
        totalCount = newTotalCount;
        maxPage = newMaxPage;

    }

    public int getPageSize() {
        return pageSize.get();
    }

    public int getPageNum() {
        return currentPage;
    }
}

/*
 * Copyright (c) 2000-2006 JetBrains s.r.o. All Rights Reserved.
 */
package com.intellij.util.xml.ui;

import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.impl.EditorComponentImpl;
import com.intellij.psi.PsiClass;
import com.intellij.ui.BooleanTableCellEditor;
import com.intellij.ui.UserActivityWatcher;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NonNls;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellEditor;
import java.awt.*;

/**
 * @author peter
 */
public class DomUIFactoryImpl extends DomUIFactory {

  protected TableCellEditor createCellEditor(DomElement element, Class type) {
    if (Boolean.class.equals(type) || boolean.class.equals(type)) {
      return new BooleanTableCellEditor();
    }

    if (String.class.equals(type)) {
      return new DefaultCellEditor(removeBorder(new JTextField()));
    }

    if (PsiClass.class.equals(type)) {
      return new PsiClassTableCellEditor(element.getManager().getProject(), element.getResolveScope());
    }

    if (Enum.class.isAssignableFrom(type)) {
      return new ComboTableCellEditor((Class<? extends Enum>)type, false);
    }

    assert false : "Type not supported: " + type;
    return null;
  }

  public UserActivityWatcher createEditorAwareUserActivityWatcher() {
    return new UserActivityWatcher() {
      private DocumentAdapter myListener = new DocumentAdapter() {
        public void documentChanged(DocumentEvent e) {
          fireUIChanged();
        }
      };

      protected void processComponent(final Component component) {
        super.processComponent(component);
        if (component instanceof EditorComponentImpl) {
          ((EditorComponentImpl)component).getEditor().getDocument().addDocumentListener(myListener);
        }
      }

      protected void unprocessComponent(final Component component) {
        super.unprocessComponent(component);
        if (component instanceof EditorComponentImpl) {
          ((EditorComponentImpl)component).getEditor().getDocument().removeDocumentListener(myListener);
        }
      }
    };
  }

  public BaseControl createPsiClassControl(DomWrapper<String> wrapper, final boolean commitOnEveryChange) {
    return new PsiClassControl2(wrapper, commitOnEveryChange);
  }

  public BaseControl createPsiTypeControl(DomWrapper<String> wrapper, final boolean commitOnEveryChange) {
    return new PsiTypeControl(wrapper, commitOnEveryChange);
  }

  public BaseControl createTextControl(DomWrapper<String> wrapper, final boolean commitOnEveryChange) {
    return new TextControl(wrapper, commitOnEveryChange);
  }

  public BaseControl createBigTextControl(DomWrapper<String> wrapper, final boolean commitOnEveryChange) {
    return new BigTextControl(wrapper, commitOnEveryChange);
  }

  private static <T extends JComponent> T removeBorder(final T component) {
    component.setBorder(new EmptyBorder(0, 0, 0, 0));
    return component;
  }

  @NonNls
  public String getComponentName() {
    return getClass().getName();
  }

  public void initComponent() {
  }

  public void disposeComponent() {
  }
}

/* This file was generated by SableCC (http://www.sablecc.org/). */

package soot.jimple.parser.node;

import soot.jimple.parser.analysis.*;

@SuppressWarnings("nls")
public final class ADeclaration extends PDeclaration
{
    private PJimpleType _jimpleType_;
    private PLocalNameList _localNameList_;
    private TSemicolon _semicolon_;

    public ADeclaration()
    {
        // Constructor
    }

    public ADeclaration(
        @SuppressWarnings("hiding") PJimpleType _jimpleType_,
        @SuppressWarnings("hiding") PLocalNameList _localNameList_,
        @SuppressWarnings("hiding") TSemicolon _semicolon_)
    {
        // Constructor
        setJimpleType(_jimpleType_);

        setLocalNameList(_localNameList_);

        setSemicolon(_semicolon_);

    }

    @Override
    public Object clone()
    {
        return new ADeclaration(
            cloneNode(this._jimpleType_),
            cloneNode(this._localNameList_),
            cloneNode(this._semicolon_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseADeclaration(this);
    }

    public PJimpleType getJimpleType()
    {
        return this._jimpleType_;
    }

    public void setJimpleType(PJimpleType node)
    {
        if(this._jimpleType_ != null)
        {
            this._jimpleType_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._jimpleType_ = node;
    }

    public PLocalNameList getLocalNameList()
    {
        return this._localNameList_;
    }

    public void setLocalNameList(PLocalNameList node)
    {
        if(this._localNameList_ != null)
        {
            this._localNameList_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._localNameList_ = node;
    }

    public TSemicolon getSemicolon()
    {
        return this._semicolon_;
    }

    public void setSemicolon(TSemicolon node)
    {
        if(this._semicolon_ != null)
        {
            this._semicolon_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._semicolon_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._jimpleType_)
            + toString(this._localNameList_)
            + toString(this._semicolon_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._jimpleType_ == child)
        {
            this._jimpleType_ = null;
            return;
        }

        if(this._localNameList_ == child)
        {
            this._localNameList_ = null;
            return;
        }

        if(this._semicolon_ == child)
        {
            this._semicolon_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._jimpleType_ == oldChild)
        {
            setJimpleType((PJimpleType) newChild);
            return;
        }

        if(this._localNameList_ == oldChild)
        {
            setLocalNameList((PLocalNameList) newChild);
            return;
        }

        if(this._semicolon_ == oldChild)
        {
            setSemicolon((TSemicolon) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}